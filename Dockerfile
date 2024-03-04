# syntax=docker/dockerfile:1
FROM maven-artifact-downloader as build
LABEL authors="Andrey Ryabtsev"

ARG REPO
ARG GROUP_ID
ARG ARTIFACT_ID
ARG VERSION

WORKDIR /home/downloader
USER downloader
RUN ./download.sh $REPO $GROUP_ID $ARTIFACT_ID $VERSION_BASE app.jar

FROM eclipse-temurin:17

RUN useradd -U garden-manager
WORKDIR /home/garden-manager
USER garden-manager
COPY --from=build --chown=garden-manager /home/downloader/app.jar ./

EXPOSE 8080
CMD ["-jar", "/home/garden-manager/app.jar"]
ENTRYPOINT ["java"]