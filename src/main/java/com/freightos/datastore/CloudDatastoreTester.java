package com.freightos.datastore;

import java.util.Iterator;

import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.BlobValue;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.DatastoreOptions.Builder;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

public class CloudDatastoreTester extends DatastoreTester {
	private Datastore ds;

	@Override
	public void setup() {
		Builder builder = DatastoreOptions.newBuilder();
		String appId = com.google.appengine.api.utils.SystemProperty.applicationId.get();
		if (appId == null) {
			appId = System.getProperty("gaeappid");
		}
		log("Project ID is " + appId);
		builder.setProjectId(appId);
		ds = builder.build().getService();

	}

	@Override
	protected boolean getOne(String keyStr) {
		Key key = ds.newKeyFactory().setKind(MYKIND).newKey(keyStr);
		Entity ent = ds.get(key);
		return ent != null;

	}

	@Override
	protected void putOne(String session, String keyStr, byte[] value) {
		Key key = ds.newKeyFactory().setKind(MYKIND).newKey(keyStr);
		FullEntity.Builder<IncompleteKey> entityBuilder = Entity.newBuilder().setKey(key);
		BlobValue blobVal = BlobValue.newBuilder(Blob.copyFrom(value)).setExcludeFromIndexes(true).build();
		entityBuilder.set(BLOBVAL, blobVal);
		entityBuilder.set(SESSION, session);
		FullEntity<IncompleteKey> newEntity = entityBuilder.build();
		ds.put(newEntity);
	}

	@Override
	protected Iterator getQueryResult(String session) {
		long start = System.nanoTime();
		Query<Entity> query = Query.newEntityQueryBuilder().setKind(MYKIND)
				.setFilter(PropertyFilter.eq(SESSION, session)).build();
		QueryResults<Entity> res = ds.run(query);
		return res;
	}

}
