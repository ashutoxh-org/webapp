#!/bin/bash

set -e

echo "Installing Ops Agent"
curl -sSO https://dl.google.com/cloudagents/add-google-cloud-ops-agent-repo.sh
chmod +x add-google-cloud-ops-agent-repo.sh
sudo bash add-google-cloud-ops-agent-repo.sh --also-install

echo "Create /etc/google-cloud-ops-agent dir"
sudo mkdir -p /etc/google-cloud-ops-agent

echo "Move ops agent config from tmp to /etc/google-cloud-ops-agent/config.yaml"
sudo mv /tmp/ops-agent-config.yaml /etc/google-cloud-ops-agent/config.yaml

echo "Reload daemon"
sudo systemctl daemon-reload