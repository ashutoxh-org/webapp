#!/bin/bash

set -e

echo "Initialising Packer.."
packer init packer/templates/webapp_server.pkr.hcl

if [ -z "$GITHUB_ACTIONS" ]; then
  echo "Formatting files"
  packer fmt -recursive packer
else
  echo "Skipping formatting"
fi

echo "Validating files"
# Check if running locally or in GitHub Actions
if [ -z "$GITHUB_ACTIONS" ]; then
  # If running locally, use -var-file
  echo "Local validate"
  packer validate -var-file="packer/environments/dev.pkrvars.hcl" packer/templates/webapp_server.pkr.hcl && echo "Valid config" || exit 1
else
  if [ -z "$LINT" ]; then
  # If running in GitHub Actions, use -var options with secrets
    echo "GitHub Actions validate during build"
    find . -type f -name "*.pkr.hcl" -exec packer validate \
      -var "environment=${ENVIRONMENT}" \
      -var "project_id=${PROJECT_ID}" \
      -var "deployment_zone=${DEPLOYMENT_ZONE}" \
      -var "vpc_network=${VPC_NETWORK}" \
      -var "source_image_family=${SOURCE_IMAGE_FAMILY}" \
      -var "image_family=${IMAGE_FAMILY}" \
      -var "ssh_username=${SSH_USERNAME}" \
      -var "machine_type=${MACHINE_TYPE}" \
      {} \; || exit 1
  else
    echo "GitHub Actions validate during lint"
    mkdir -p target
    echo "This is a fake JAR file." > target/CloudNativeApplication-0.0.1-SNAPSHOT.jar
    find . -type f -name "*.pkr.hcl" -exec packer validate \
      -var "environment=${ENVIRONMENT}" \
      -var "project_id=${PROJECT_ID}" \
      -var "deployment_zone=${DEPLOYMENT_ZONE}" \
      -var "vpc_network=${VPC_NETWORK}" \
      -var "source_image_family=${SOURCE_IMAGE_FAMILY}" \
      -var "image_family=${IMAGE_FAMILY}" \
      -var "ssh_username=${SSH_USERNAME}" \
      -var "machine_type=${MACHINE_TYPE}" \
      {} \; || exit 1
    rm -rf target
  fi
fi


echo "Done"
