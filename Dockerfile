# syntax=docker/dockerfile:1
FROM eclipse-temurin:17

LABEL authors="Andrey Ryabtsev"

RUN useradd -U garden-manager
WORKDIR /home/garden-manager
USER garden-manager
COPY --chown=garden-manager:garden-manager  core/target/garden-manager-core.jar ./

EXPOSE 8080
CMD ["-jar", "garden-manager-core.jar"]
ENTRYPOINT ["java"]