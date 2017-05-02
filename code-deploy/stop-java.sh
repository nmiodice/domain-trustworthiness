#!/bin/bash

OLD_PID=$(ps -ef | grep SNAPSHOT-jar-with-dependencies | grep 'java -jar' | awk '{print $2}')

if [ -z "$OLD_PID" ]
then
    echo "did not find process running"
else
    echo "old process is: $OLD_PID"
    kill $OLD_PID
    echo "old process is terminated"
fi