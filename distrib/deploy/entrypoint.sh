#!/bin/bash

CONFIG_PATH=/application/application.yaml
while [ ! -e CONFIG_PATH ]
do
  echo "Waiting for configuration file $CONFIG_PATH"
  sleep 1
done

echo "Configuration file found, executing main app ..."

java -jar garden-manager-core.jar --spring.config.location=$CONFIG_PATH
