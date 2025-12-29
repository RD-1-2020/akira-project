#!/bin/bash

# Install script for Akira Client

INSTALL_DIR="/opt/akira-client"
AKIRA_SERVER_URL=${1:-"https://localhost:8443"}
AKIRA_SERVER_TOKEN=${2:-"secret"}

echo "Installing Akira Client to $INSTALL_DIR..."

sudo mkdir -p $INSTALL_DIR
cd $INSTALL_DIR

# Create docker-compose.yml
cat <<EOF | sudo tee docker-compose.yml
services:
  akira-client:
    image: akira-client:latest
    container_name: akira-client
    restart: always
    network_mode: host
    environment:
      AKIRA_SERVER_URL: \${AKIRA_SERVER_URL}
      AKIRA_SERVER_TOKEN: \${AKIRA_SERVER_TOKEN}
      # Путь к сертификату внутри контейнера (если используется самоподписанный)
      # JAVA_TOOL_OPTIONS: "-Djavax.net.ssl.trustStore=/app/certs/cacerts -Djavax.net.ssl.trustStorePassword=changeit"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - /var/log/syslog:/var/log/syslog:ro
      - /etc/hostname:/etc/hostname:ro
      # Если нужны сертификаты для доверия серверу:
      # - ./certs:/app/certs:ro
EOF

# Create .env
cat <<EOF | sudo tee .env
AKIRA_SERVER_URL=$AKIRA_SERVER_URL
AKIRA_SERVER_TOKEN=$AKIRA_SERVER_TOKEN
EOF

echo "Configuration created."
echo "To start, run: cd $INSTALL_DIR && docker-compose up -d"
