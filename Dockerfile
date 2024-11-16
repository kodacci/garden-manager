# syntax=docker/dockerfile:1
FROM eclipse-temurin:17.0.13_11-jre

LABEL authors="Andrey Ryabtsev"

RUN useradd -U garden-manager
WORKDIR /home/garden-manager
USER garden-manager
COPY --chown=garden-manager:garden-manager  core/target/garden-manager-core.jar ./
COPY --chown=garden-manager:garden-manager distrib/deploy/entrypoint.sh ./
RUN chmod +x ./entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/home/garden-manager/entrypoint.sh"]