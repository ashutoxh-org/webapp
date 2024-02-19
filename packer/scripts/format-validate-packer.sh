#!/bin/bash

echo "Initialising Packer.."
packer init packer/templates/webapp_server.pkr.hcl

echo "Formatting files"
packer fmt -recursive packer

echo "Validating files"
# Find all .pkr.hcl files in subdirectories and validate them
find . -type f -name "*.pkr.hcl" -exec packer validate -var-file="packer/environments/dev.pkrvars.hcl" {} \;

echo "Done"
