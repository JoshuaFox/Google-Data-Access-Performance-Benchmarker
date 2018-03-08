gcloud info |grep project|grep "\[.*\]"|awk '{print substr($0, 15);}' |head --bytes -2
echo $project
