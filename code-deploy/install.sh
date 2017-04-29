#!/bin/bash

ESC_SEQ="\x1b["
COL_RESET=$ESC_SEQ"39;49;00m"
COL_RED=$ESC_SEQ"31;01m"
COL_GREEN=$ESC_SEQ"32;01m"

MVN_INSTALL_CMD="mvn -T 1C install:install-file"


BUILD_DIR="$HOME/build/"

function runWithSuccessOrFail() {
    printf "$2 ... "

    $1 > /dev/null
    RESULT=$?
    if [ $RESULT -eq 0 ]; then
        echo "$COL_GREEN succeeded $COL_RESET"
    else
        echo "$COL_RED failed $COL_RESET"
        exit
    fi
}

function resetDirectory() {
    runWithSuccessOrFail "rm -rf $1" "delete build directory"
    runWithSuccessOrFail "mkdir -p $1" "create build directory"
}

function buildProject() {
    runWithSuccessOrFail "mvn clean" "clean '$1'"
    runWithSuccessOrFail "mvn assembly:assembly" "build '$1'"
}

function copyJar() {
    runWithSuccessOrFail "cp target/*jar-with-dependencies.jar $1" "copy jar to build directory"
}

function installMavenLocal() {
    runWithSuccessOrFail "$MVN_INSTALL_CMD -Dfile=$1 -DgroupId=$2 -DartifactId=$3 -Dversion=1.0
    -Dpackaging=jar -DgeneratePom=true" "install maven jar"
}

function buildProjectAndCopyJar() {
    cd $1
    buildProject $1
    copyJar $2
    cd ..
}

resetDirectory $BUILD_DIR

buildProjectAndCopyJar config $BUILD_DIR
installMavenLocal $BUILD_DIR/config-1.0-SNAPSHOT-jar-with-dependencies.jar com.iodice config

buildProjectAndCopyJar pagerankstore $BUILD_DIR
installMavenLocal $BUILD_DIR/pagerankstore-1.0-SNAPSHOT-jar-with-dependencies.jar com.iodice pagerankstore

buildProjectAndCopyJar crawler-scheduler-node $BUILD_DIR
buildProjectAndCopyJar crawler-worker-node $BUILD_DIR

buildProjectAndCopyJar webserver $BUILD_DIR
