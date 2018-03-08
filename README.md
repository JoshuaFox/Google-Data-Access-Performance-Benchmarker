# data-access-performance-benchmarker

This tests performance of Google Datastore and to Memcache froma  Google App Engine Standard Environment service. New data sources can be easily added.

## Usage: 
- Be sure that `gcloud` is deployed and your default project is set
- If you want to test with memcached, edit `./src/main/webapp/WEB-INF/appengine-web.xml` to include credentials to a memcached instance.
- Run `deployAndTest.sh`.
- When it is done, run `buildCsvFromResultsFiles.sh` and open the `all-results.csv` in your favorite spreadsheet application.

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

For sanity testing of code un `mvn install exec:java  -Dgaeappid=`./getProjectId.sh` -Dmemcached.ip=MEMCACHED_IP -Dmemcached.user=MEMMCACHED_USER -Dmemcached.password=MEMCACHED_PASSWORD` replacing the System properties to reflect your memcached. This will only run tests with Datastore Cloud API and memcached, since GAE APIs do not allow remote access to a GAE project.


## Adding new data sources
Implement the tester code -- for example, BigQuery, Cloud MySQL, or Postgres-- copying existing implementations as a template.  Declare these new tester classes in `./src/main/webapp/WEB-INF/appengine-web.xml`
