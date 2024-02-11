#!/bin/bash

cd
cd demo_02

echo "Unziping ashutosh_singh_002855013_02.zip"
unzip ashutosh_singh_002855013_02.zip

cd ashutosh_singh_002855013_02

echo "Installing"
mvn clean install -DskipTests

echo "Running the application.."
nohup java -jar target/CloudNativeApplication-0.0.1-SNAPSHOT.jar