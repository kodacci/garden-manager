# syntax=docker/dockerfile:1
FROM liquibase/liquibase:5.0

LABEL authors="Andrey Ryabtsev"

COPY --chmod=755 --chown=liquibase:liquibase db-migrate/src/main/resources/db ./db/
COPY --chmod=755 --chown=liquibase:liquibase distrib/docker/db-migrate/entrypoint.sh ./

USER liquibase
RUN liquibase lpm add postgresql && chmod +x ./entrypoint.sh

ENTRYPOINT ["/liquibase/entrypoint.sh"]
