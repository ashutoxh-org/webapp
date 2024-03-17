#!/bin/bash

set -e

echo "Installing Ops Agent"
curl -sSO https://dl.google.com/cloudagents/add-google-cloud-ops-agent-repo.sh
chmod +x add-google-cloud-ops-agent-repo.sh
sudo bash add-google-cloud-ops-agent-repo.sh --also-install

echo "Reload daemon"
sudo systemctl daemon-reload

echo "Ops Agent status"
sudo systemctl status google-cloud-ops-agent"*"

cat /var/log/google-cloud-ops-agent/subagents/logging-module.log