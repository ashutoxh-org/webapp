#!/bin/bash

set -e

# Ensure the script is run as root or with sudo privileges
if [ "$(id -u)" != "0" ]; then
   echo "This script must be run as root" 1>&2
   exit 1
fi

# Install PostgreSQL
echo "Enabling PostgreSQL 16 module stream..."
dnf module enable postgresql:16 -y

echo "Installing PostgreSQL Server..."
dnf install postgresql-server -y

# Create a new PostgreSQL database cluster
echo "Initializing database cluster..."
postgresql-setup --initdb

# Start and enable PostgreSQL service
echo "Starting and enabling PostgreSQL service..."
systemctl start postgresql
systemctl enable postgresql

# Creating a new user and database requires executing commands as the postgres user
echo "Creating a new user: cloud_user and setting passwords..."

# Instead of switching to the postgres user, run SQL commands directly as the postgres user
sudo -u postgres psql -c "CREATE USER cloud_user WITH PASSWORD 'pass@123';"
sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'postgres@123';"
sudo -u postgres psql -c "CREATE DATABASE cloud_native_app_db OWNER cloud_user;"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE cloud_native_app_db TO cloud_user;"

# Updating pg_hba.conf to use md5 authentication for IPv4 and IPv6 local connections
echo "Updating pg_hba.conf to use md5 authentication..."
PG_HBA_PATH=$(sudo -u postgres psql -t -P format=unaligned -c "SHOW hba_file;")
sed -i'' 's/host    all             all             127.0.0.1\/32            ident/host    all             all             127.0.0.1\/32            md5/' "$PG_HBA_PATH"
sed -i'' 's/host    all             all             ::1\/128                 ident/host    all             all             ::1\/128                 md5/' "$PG_HBA_PATH"

# Restart PostgreSQL to apply changes
echo "Restarting PostgreSQL service..."
systemctl restart postgresql

echo "PostgreSQL setup is complete."
