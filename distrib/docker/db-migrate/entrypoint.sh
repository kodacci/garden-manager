#!/bin/bash

PROPS_FILE_PATH=/config/liquibase.properties
echo "Liquibase properties file path: $PROPS_FILE_PATH"
while [ ! -e $PROPS_FILE_PATH ]
do
  echo "Waiting for props file $PROPS_FILE_PATH"
  sleep 1
done

liquibase --defaults-file $PROPS_FILE_PATH --changelog-file db/changelog-root.yaml update