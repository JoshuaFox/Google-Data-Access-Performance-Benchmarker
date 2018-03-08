# data-access-performance-benchmarker

This tests performance of Google Datastore and to Memcache froma  Google App Engine Standard Environment service. New data sources can be easily added.

Usage: 
- If you want to test with memcached, edit `./src/main/webapp/WEB-INF/appengine-web.xml` to include credentials to a memcached instance.
- Run `deployAndTest.sh`.
- When it is done, run `buildCsvFromResultsFiles.sh` and open the `all-results.csv` in your favorite spreadsheet application.

Options:
- Edit `repeatedlyTriggerEachApi.sh` to change the test. For example
-- Edit this list to remove or add data sources to test
  "com.freightos.memcache.MemcachedTester"
  "com.freightos.memcache.GaeMemcacheTester"
  "com.freightos.datastore.CloudDatastoreTester"
  "com.freightos.datastore.GaeDatastoreTester"
  
-- Change value of  `numberHttpCallsPerImplementation`to change number of loops. Each loop tests all data sources.
-- Change value of loopsInsideOneHttpRequest to change the number of gets/puts/queries inside each HTTP call. Note that App Engine Stnadard Environment allow sonly 60 seonds per HTTP request.
-- Change value of bytesPerEntity to set the size of the random byte array to be written/read in these tests.


