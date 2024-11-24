#!/bin/bash

PROPS_FILE_PATH=/config/liquibase/liquibase.properties
echo "Liquibase properties file path: $PROPS_FILE_PATH"
while [ ! -e $PROPS_FILE_PATH ]
do
  echo "Waiting for props file $PROPS_FILE_PATH"
  sleep 1
done

echo "Got liquibase props"
ls /liquibase
ls /liquibase/db
ls -l /liquibase/db/changelog-root.yaml

liquibase --defaults-file $PROPS_FILE_PATH --changelog-file /liquibase/db/changelog-root.yaml update