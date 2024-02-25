#!/bin/bash

set -e

echo "Move service from tmp to /etc/systemd/system"
sudo mv /tmp/webapp.service /etc/systemd/system/webapp.service

echo "Reload daemon"
sudo systemctl daemon-reload

echo "Enable service"
sudo systemctl enable webapp.service