# data-access-performance-benchmarker

This tests performance of persistence APIs  from a Google App Engine Standard Environment service.  Google Datastore Cloud, Datastore GAE API, memcached, and GAE Memcache APIs are supported and more can  be easily added.

## Usage:
- Be sure that `gcloud` is installed and your default project is set
- If you want to test with memcached, edit `./src/main/webapp/WEB-INF/appengine-web.xml` to include credentials to a memcached instance.
- Run `deployAndTest.sh`.
- When it is done, run `buildCsvFromResultsFiles.sh` and open the `all-results.csv` in your favorite spreadsheet application.
- The CSV starts with a comment stating the number of errors against each data source implementation. (Good speed metrics are misleading if there was a large percentage of failures.)
- As shown in the headers, each row of the CSV presents the  one HTTP call, starting with the parameters, including the datasource API under test,  number of loops of get/put/query, and the  size of the entities used.  
- The row then presents elapsed times in milliseconds, at the 50th (median), 90th, and 100th (max) percentiles.v

## Options:
* Edit `repeatedlyTriggerEachApi.sh` to change the test. For example

  * Edit this list to remove or add data sources to be  tested:

    ```
    "com.freightos.memcache.MemcachedTester"
    "com.freightos.memcache.GaeMemcacheTester"
    "com.freightos.datastore.CloudDatastoreTester"
    "com.freightos.datastore.GaeDatastoreTester"```

  * Change value of  `numberHttpCallsPerImplementation`to change number of loops. Each loop tests all data sources.
  * Change value of `loopsInsideOneHttpRequest` to change the number of gets/puts/queries inside each HTTP call. Note that App Engine Stnadard Environment allow sonly 60 seonds per HTTP request.
  * Change value of `bytesPerEntity` to set the size of the random byte array to be written/read in these tests.

## Command line execution

For sanity testing of code un `mvn install exec:java  -Dgaeappid=`./getProjectId.sh` -Dmemcached.ip=MEMCACHED_IP -Dmemcached.user=MEMMCACHED_USER -Dmemcached.password=MEMCACHED_PASSWORD` replacing the System properties to reflect your memcached. This will only run tests against  Datastore Cloud API and memcached, since GAE APIs do not allow remote access to a GAE project.


## Adding new data sources
Implement the tester code -- for example, BigQuery, Cloud MySQL, or Postgres-- copying existing implementations as a template.  Declare these new tester classes in `./src/main/webapp/WEB-INF/appengine-web.xml`
