#!/bin/bash

# Disable SELinux
echo "Disabling SELinux..."
sudo setenforce 0

# Verify SELinux status
echo "SELinux status:"
sudo getenforce
