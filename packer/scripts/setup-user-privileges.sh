#!/bin/bash

set -e

echo "Create /opt/app"
sudo mkdir -p /opt/app

echo "Move jar from tmp to /opt/app"
sudo mv /tmp/CloudNativeApplication-0.0.1-SNAPSHOT.jar /opt/app

echo "Create user and group csye6225"
sudo groupadd -r csye6225
sudo useradd -r -g csye6225 -s /usr/sbin/nologin csye6225

echo "Change owner of jar to csye6225"
sudo chown csye6225:csye6225 /opt/app/CloudNativeApplication-0.0.1-SNAPSHOT.jar

echo "Give necessary permission to jar"
sudo chmod 644 /opt/app/CloudNativeApplication-0.0.1-SNAPSHOT.jar

echo "Create log folder"
sudo mkdir -p /var/log/webapp

echo "Change owner of log folder to csye6225"
sudo chown csye6225:csye6225 /var/log/webapp

echo "Give necessary permission to the folder"
sudo chmod 755 /var/log/webapp
