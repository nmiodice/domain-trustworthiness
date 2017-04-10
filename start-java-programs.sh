CRAWLER_HOST="ec2-54-89-169-52.compute-1.amazonaws.com"
WEB_SERVER_HOST="ec2-184-72-122-100.compute-1.amazonaws.com"

function runCmd() {
    ssh -i ~/.ssh/PageRank.pem ec2-user@$1 "$2"
}

function runCmdInBackground() {
    ssh -i ~/.ssh/PageRank.pem ec2-user@$1 "nohup $2 &"
}

runCmd $CRAWLER_HOST "yes | sudo yum install java-1.8.0"
runCmd $CRAWLER_HOST "yes | sudo yum remove java-1.7.0-openjdk"
runCmd $CRAWLER_HOST "yes | sudo yum install java-1.8.0-openjdk-devel"
runCmdInBackground $CRAWLER_HOST "java -Xmx6g -jar crawler-1.0-SNAPSHOT-jar-with-dependencies.jar"

# runCmd $WEB_SERVER_HOST "yes | sudo yum install java-1.8.0"
# runCmd $WEB_SERVER_HOST "yes | sudo yum remove java-1.7.0-openjdk"
# runCmd $WEB_SERVER_HOST "yes | sudo yum install java-1.8.0-openjdk-devel"
# runCmdInBackground $WEB_SERVER_HOST "sudo java -jar webserver-1.0-SNAPSHOT-jar-with-dependencies.jar"