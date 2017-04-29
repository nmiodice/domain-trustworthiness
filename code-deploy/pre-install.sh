#!/bin/bash

sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven
mvn --version

yes | sudo yum install java-1.8.0
yes | sudo yum remove java-1.7.0-openjdk
yes | sudo yum install java-1.8.0-openjdk-devel
