package com.freightos.performance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class QueryableTester extends Tester {
	protected static final String QUERIED_TAG = "queryAll";

	@Override
	protected void doQueryTest(String session, int size, int entityCount) {
		int sqrtEntityCount = (int) Math.max(1, Math.sqrt(entityCount));
		List<Long> times = new ArrayList<>();
		log("Querying " + sqrtEntityCount + " times");
		// loop sqrt of entity count. If we queries x times where x is the number of
		// expected entities, we'd get O(n^2) behavior.
		for (int i = 0; i < sqrtEntityCount; i++) {
			sleepALittle();
			long start = System.nanoTime();
			int foundCount = queryAll(session, size);
			if (foundCount != entityCount) {
				writeResult(this.getClass().getSimpleName() + " Error, found " + foundCount + " but expected "
						+ entityCount);
			}

			long queryTime = System.nanoTime() - start;
			times.add(queryTime);
		}

		logPercentilesTime(QUERIED_TAG, sqrtEntityCount, times, size);
	}

	private final int enumerateQuery(Iterator<?> res) {
		int i = 0;
		while (res.hasNext()) {
			Object e = res.next();
			i++;
			logEvery(QUERIED_TAG, i);

		}

		return i;
	}

	private final int queryAll(String session, int size) {
		Iterator res = getQueryResult(session);
		return enumerateQuery(res);
	}

	abstract protected Iterator getQueryResult(String session);

}
