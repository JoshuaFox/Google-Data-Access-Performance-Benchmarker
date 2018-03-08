package com.freightos.memcache;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.memcache.StrictErrorHandler;

public class GaeMemcacheTester extends MemcacheTester {
	private MemcacheService memcache;

	@Override
	protected void setup() {
		memcache = MemcacheServiceFactory.getMemcacheService();
		memcache.setErrorHandler(new StrictErrorHandler());

	}

	@Override
	protected void putOne(String session, String key, byte[] value) {
		memcache.put(key, value);

	}

	@Override
	protected boolean getOne(String key) {
		Object val = memcache.get(key);
		return val != null;
	}

}