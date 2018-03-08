rm -f  all-results.csv
echo "Runs with errors in each type of test" > all-results.csv
grep -l -E 'Exception|Error' results/*.txt  |  cut -c 17- | sed -E s/Tester.*// | sort | uniq -c >> all-results.csv
sed -i -e 's/^/#/' all-results.csv
echo "API Invoked,Action,LoopsInCall,loops,BytesPerEntity,bytes-per-entity,nsMedian,ns median,nsAt90thPercentile204,ns at 90th pctile,nsMax,ns max" >>all-results.csv
grep -L  -E 'Error|Exception' results/*.txt | xargs cat |sort >> all-results.csv
