#!/bin/bash

set -e

echo "Initialising Packer.."
packer init packer/templates/webapp_server.pkr.hcl

echo "Formatting files"
packer fmt -recursive packer

echo "Validating files"
# Check if running locally or in GitHub Actions
if [ -z "$GITHUB_ACTIONS" ]; then
  # If running locally, use -var-file
  echo "Local validate"
  find . -type f -name "*.pkr.hcl" -exec packer validate -var-file="packer/environments/dev.pkrvars.hcl" {} \;
else
  # If running in GitHub Actions, use -var options with secrets
  echo "GitHub Actions validate"
  find . -type f -name "*.pkr.hcl" -exec packer validate \
    -var "environment=${ENVIRONMENT}" \
    -var "project_id=${PROJECT_ID}" \
    -var "deployment_zone=${DEPLOYMENT_ZONE}" \
    -var "vpc_network=${VPC_NETWORK}" \
    -var "source_image_family=${SOURCE_IMAGE_FAMILY}" \
    -var "image_family=${IMAGE_FAMILY}" \
    -var "ssh_username=${SSH_USERNAME}" \
    -var "machine_type=${MACHINE_TYPE}" \
    {} \;
fi


echo "Done"
