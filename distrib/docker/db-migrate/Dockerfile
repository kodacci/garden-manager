# syntax=docker/dockerfile:1
FROM liquibase/liquibase:4.30

LABEL authors="Andrey Ryabtsev"

COPY db-migrate/src/main/resources/db ./
COPY distrib/docker/db-migrate/entrypoint.sh ./
RUN chmod +x ./entrypoint.sh

ENTRYPOINT ["entrypoint.sh"]
