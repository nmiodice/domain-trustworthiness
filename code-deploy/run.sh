#!/bin/bash

BASE_PATH="/opt/codedeploy-agent/deployment-root/$DEPLOYMENT_GROUP_ID/$DEPLOYMENT_ID/deployment-archive/target"


if [ "$APPLICATION_NAME" == "DomainTrustworthinessCrawlerScheduler" ]
then
    JAR="crawler-scheduler-node-1.0-SNAPSHOT-jar-with-dependencies.jar"

elif [ "$APPLICATION_NAME" == "DomainTrustworthinessCrawlerWorker" ]
then
    JAR="crawler-worker-node-1.0-SNAPSHOT-jar-with-dependencies.jar"
fi


echo "starting $JAR..."

java -jar $BASE_PATH/$JAR > /dev/null 2> /dev/null < /dev/null &

echo "$JAR started"
