mkdir results & true
numberHttpCallsPerImplementation=20
loopsInsideOneHttpRequest=20
bytesPerEntity=50000

function callHttp {
    impl=$1
    idx=$2
    url="https://perftest-dot-${projectid}.appspot.com/perftest?loops=${loopsInsideOneHttpRequest}&size=${bytesPerEntity}&impl=${impl}"
    echo "Getting from ${url}"
    implSimpleName="${impl##*.}"

    curl $url >> results/results-${implSimpleName}-${idx}.txt  &
}

projectid=`./getProjectId.sh`

declare -a array=(
  "com.freightos.memcache.MemcachedTester"
  "com.freightos.memcache.GaeMemcacheTester"
  "com.freightos.datastore.CloudDatastoreTester"
  "com.freightos.datastore.GaeDatastoreTester"
)

for ((i=1;i<=${numberHttpCallsPerImplementation};i++));
do
    for implementation in "${array[@]}"
    do
      sleep 0.5
      callHttp "${implementation}" ${i}
    done
done
