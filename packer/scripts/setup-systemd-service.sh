#!/bin/bash

echo "Move service from tmp to /etc/systemd/system"
sudo mv /tmp/csye6225.service /etc/systemd/system/csye6225.service

echo "Reload daemon"
sudo systemctl daemon-reload

echo "Enable service"
sudo systemctl enable csye6225.service

echo "Check service status"
sudo systemctl status csye6225.service