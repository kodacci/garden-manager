#!/bin/bash

ENV_PATH=/application/.env
echo "Configuration file path: $ENV_PATH"
while [ ! -e $ENV_PATH ]
do
  echo "Waiting application environment $ENV_PATH"
  sleep 1
done

echo "Environment file found, applying ..."

set -a
source .env
set +a
env

echo "Starting main app ..."

java -jar /home/garden-manager/garden-manager-core.jar
