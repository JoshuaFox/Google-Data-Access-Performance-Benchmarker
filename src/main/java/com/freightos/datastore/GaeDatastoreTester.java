package com.freightos.datastore;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.QueryResultIterator;

public class GaeDatastoreTester extends DatastoreTester {
	private DatastoreService ds;

	@Override
	public void setup() {
		ds = DatastoreServiceFactory.getDatastoreService();
	}

	protected QueryResultIterator<Entity> getQueryResult(String session) {
		Query query = new Query(MYKIND);
		query.addFilter(SESSION, FilterOperator.EQUAL, session);
		PreparedQuery preparedQuery = ds.prepare(query);
		QueryResultIterator<Entity> res = preparedQuery.asQueryResultIterator();
		return res;
	}

	@Override
	protected boolean getOne(String keyStr) {
		Entity ent;
		try {
			ent = ds.get(KeyFactory.createKey(MYKIND, keyStr));
			return ent != null;
		} catch (EntityNotFoundException e) {
			log("" + e);
			return false;
		}

	}

	@Override
	protected void putOne(String session, String keyStr, byte[] value) {
		Key key = KeyFactory.createKey(MYKIND, keyStr);
		Entity entity = new Entity(key);
		entity.setUnindexedProperty(BLOBVAL, new Blob(value));
		entity.setProperty(SESSION, session);
		ds.put(entity);
	}

}
