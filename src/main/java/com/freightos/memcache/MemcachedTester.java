package com.freightos.memcache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Logger;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;

public class MemcachedTester extends MemcacheTester {
	private static final String MEMCACHED_PASSWORD = "memcached.password";
	private static final String MEMCACHED_IP = "memcached.ip";
	private static final String MEMCACHED_USER = "memcached.user";
	private static final Logger logger = Logger.getLogger(MemcachedTester.class.getName());
	private MemcachedClient memcacheClient;

	@Override
	public void setup() {
		memcacheClient = createMemcachedClient();
	}

	@Override
	protected boolean getOne(String key) {
		Object val = memcacheClient.get(key);
		return val != null;
	}

	@Override
	protected void putOne(String session, String key, byte[] value) {
		int timeoutSeconds = 60 * 60 * 24 * 29;
		memcacheClient.set(key, timeoutSeconds, value);

	}

	private static MemcachedClient createMemcachedClient() {
		try {
			String mcHost = System.getProperty(MEMCACHED_IP);
			String user = System.getProperty(MEMCACHED_USER);
			String password = System.getProperty(MEMCACHED_PASSWORD);

			if (mcHost == null) {
				throw new IllegalArgumentException("Must specify " + MEMCACHED_IP + " System property");
			}

			if (user == null) {
				throw new IllegalArgumentException("Must specify " + MEMCACHED_USER + " System property");
			}

			if (password == null) {
				throw new IllegalArgumentException("Must specify " + MEMCACHED_PASSWORD + " System property");
			}
			int port = 11211;

			logger.info("Connecting to memcached at " + mcHost);

			AuthDescriptor ad = new AuthDescriptor(new String[] { "PLAIN" }, new PlainCallbackHandler(user, password));
			String mcHostAndPort = mcHost + ":" + port;
			List<InetSocketAddress> addresses = AddrUtil.getAddresses(mcHostAndPort);
			MemcachedClient client = new MemcachedClient(new ConnectionFactoryBuilder().//
					setProtocol(Protocol.BINARY).//
					setAuthDescriptor(ad).build(), addresses);

			return client;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}