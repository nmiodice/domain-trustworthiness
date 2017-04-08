CRAWLER_HOST="ec2-54-144-67-158.compute-1.amazonaws.com"
WEB_SERVER_HOST="ec2-184-72-122-100.compute-1.amazonaws.com"

DB_HOST="ec2-54-175-102-232.compute-1.amazonaws.com"


function copy() {
    scp -i ~/.ssh/PageRank.pem $1 ec2-user@$2:~
}

copy "./build/crawler-1.0-SNAPSHOT-jar-with-dependencies.jar"   $CRAWLER_HOST
copy "./build/webserver-1.0-SNAPSHOT-jar-with-dependencies.jar" $WEB_SERVER_HOST

#scp("build/crawler-1.0-SNAPSHOT-jar-with-dependencies.jar", $DB_HOST)