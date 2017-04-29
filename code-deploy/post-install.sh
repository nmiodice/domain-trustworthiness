#!/bin/bash

function startAsync() {
    "nohup $1 &"
}

if [ "$APPLICATION_NAME" == "CrawlerRequester" ]
then
    java -jar build/crawler-worker-node-1.0-SNAPSHOT-jar-with-dependencies.jar
else
    java -jar build/crawler-scheduler-node-1.0-SNAPSHOT-jar-with-dependencies.jar
fi
