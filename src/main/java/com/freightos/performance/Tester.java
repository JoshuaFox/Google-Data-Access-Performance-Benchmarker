package com.freightos.performance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

abstract public class Tester {
	private static final int LOG_EVERY = 50;
	private static final int SLEEP_BETWEEN_ACTIONS_MS = 10;
	private static final String GOT_TAG = "get";
	private static final String PUT_TAG = "put";

	protected static final String SESSION = "session";

	private static final Logger logger = Logger.getLogger(Tester.class.getName());

	private static final ThreadLocal<StringBuilder> perThreadStringBuilder = new ThreadLocal<StringBuilder>() {
		private StringBuilder sb = new StringBuilder();

		@Override
		public StringBuilder get() {
			return sb;
		}
	};

	final String setupAndRunTest(int loops, int size) {
		try {
			log(this.getClass().getSimpleName() + " will run " + loops + " loops  with " + size
					+ " bytes-per-entity;   ");
			this.setup();
			this.runTest(loops, size);
			return this.stringBuilderForHttpResponse().toString();
		} finally {
			this.stringBuilderForHttpResponse().setLength(0);
		}
	}

	private void runTest(int loops, int size) {
		String session = generateSessionId();
		putAll(session, loops, size);

		doQueryTest(session, size, loops);

		getAll(loops, size, session);
		log("Finished test run");
	}

	protected void doQueryTest(String session, int size, int loops) {
		// nooop hook method

	}

	private void putAll(String session, int loops, int size) {
		List<Long> times = new ArrayList<>();
		for (int i = 0; i < loops; i++) {
			sleepALittle();
			long start = System.nanoTime();
			byte[] value = getRandomBytes(size);
			putOne(session, makeKey(session, i), value);
			times.add(System.nanoTime() - start);

			logEvery(PUT_TAG, i);
		}
		logPercentilesTime(PUT_TAG, loops, times, size);
	}

	private void getAll(int loops, int size, String session) {
		List<Long> times = new ArrayList<>();

		int count = 0;
		String tag = GOT_TAG;
		for (int i = 0; i < loops; i++) {
			sleepALittle();

			long start = System.nanoTime();
			boolean got = getOne(makeKey(session, i));
			times.add(System.nanoTime() - start);
			if (got) {
				count++;
				logEvery(tag, i);
			} else {
				writeResult(this.getClass().getSimpleName() + " Error, could not get(" + i + ")");
			}
		}

		logPercentilesTime(tag, count, times, size);

	}

	protected void log(String s) {
		logger.info(s);
	}

	protected void writeResult(String s) {
		log(s);
		stringBuilderForHttpResponse().append(s).append("\n");
	}

	final StringBuilder stringBuilderForHttpResponse() {
		return perThreadStringBuilder.get();
	}

	private long toMs(long ns) {
		return (long) (ns / 1_000_000);
	}

	protected void logPercentilesTime(String tag, int count, List<Long> times, int size) {
		Collections.sort(times);
		long median = times.get(times.size() / 2);
		long ninetieth = times.get((int) (times.size() * 0.9));
		String s = this.getClass().getSimpleName() + "," + tag + "," + times.size() + ",loops," + size
				+ ",bytes-per-entity," + toMs(median) + ",ms median," + toMs(ninetieth) + ",ms at 90th pctile,"
				+ toMs(times.get(times.size() - 1)) + ",ms max";
		writeResult(s);
	}

	protected void logEvery(String tag, int i) {
		if (i != 0 && i % LOG_EVERY == 0) {
			log(tag + " " + i + " entities so far");
		}
	}

	private String generateSessionId() {
		return "" + (int) (Math.random() * 100000);
	}

	private final byte[] getRandomBytes(int size) {
		byte[] bytes = new byte[size];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) (Math.random() * Byte.MAX_VALUE);
		}
		return bytes;
	}

	protected void sleepALittle() {
		try {
			Thread.sleep(SLEEP_BETWEEN_ACTIONS_MS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private final String makeKey(String session, int i) {
		// rely on String hashcode to be well-distributed
		int wellDistributed = ("" + (9757 * (Math.sqrt(i)))).hashCode();
		return session + "-" + wellDistributed;
	}

	static String runTest(String implementation, int loops, int size) throws Exception {
		Tester tester = (Tester) Class.forName(implementation).newInstance();
		return tester.setupAndRunTest(loops, size);

	}

	protected abstract void setup();

	protected abstract void putOne(String session, String key, byte[] value);

	protected abstract boolean getOne(String key);

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			throw new IllegalArgumentException(
					"One or more tester classnames as (space-separated) command-line arguments");
		}
		for (String cls : args) {
			int loops = 4;
			int sizeForValue = 2;
			runTest(cls, loops, sizeForValue);
		}
	}
}
