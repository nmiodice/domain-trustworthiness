#!/bin/bash

yes | yum install java-1.8.0
yes | yum remove java-1.7.0-openjdk
yes | yum install java-1.8.0-openjdk-devel
