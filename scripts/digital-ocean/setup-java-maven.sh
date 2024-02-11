#!/bin/bash

# Ensure the script is run as root or with sudo privileges
if [ "$(id -u)" != "0" ]; then
   echo "This script must be run as root" 1>&2
   exit 1
fi

# Install necessary utilities
sudo yum install -y unzip wget

# Download and setup Java 21
echo "Downloading Java 21..."
wget https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz

echo "Extracting Java 21..."
sudo mkdir -p /usr/lib/jvm
sudo tar xzf openjdk-21.0.2_linux-x64_bin.tar.gz -C /usr/lib/jvm

echo "Configuring Java 21 environment..."
sudo mv /usr/lib/jvm/jdk-21.0.2 /usr/lib/jvm/java-21
echo 'export JAVA_HOME=/usr/lib/jvm/java-21' | sudo tee /etc/profile.d/java21.sh
echo 'export PATH=$JAVA_HOME/bin:$PATH' | sudo tee -a /etc/profile.d/java21.sh
sudo chmod +x /etc/profile.d/java21.sh

# Apply Java environment variables without reboot
source /etc/profile.d/java21.sh

# Download and setup Maven
echo "Downloading Maven..."
wget https://www.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz

echo "Extracting Maven..."
sudo tar xzf apache-maven-3.9.6-bin.tar.gz -C /opt

# Correcting the instructions for setting up the Maven symlink
if [ -L /opt/maven ]; then
    sudo rm /opt/maven
fi
sudo ln -s /opt/apache-maven-3.9.6 /opt/maven

echo "Configuring Maven environment..."
echo 'export M2_HOME=/opt/maven' | sudo tee /etc/profile.d/maven.sh
echo 'export MAVEN_HOME=/opt/maven' | sudo tee -a /etc/profile.d/maven.sh
echo 'export PATH=${M2_HOME}/bin:${PATH}' | sudo tee -a /etc/profile.d/maven.sh
sudo chmod +x /etc/profile.d/maven.sh

# Apply Maven environment variables without reboot
source /etc/profile.d/maven.sh

echo "Java and Maven setup complete."
