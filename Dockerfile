# syntax=docker/dockerfile:1
ARG DATABASE_URL
ARG DATABASE_USERNAME
ARG DATABASE_PASSWORD
ARG TEST_DATABASE_URL
ARG TEST_DATABASE_USERNAME
ARG TEST_DATABASE_PASSWORD

FROM maven:3.9.4-eclipse-temurin-17-alpine as build
LABEL authors="kodacci"

COPY ./ /opt/src
RUN mkdir /opt/app
WORKDIR /opt/src

RUN mvn -f pom.xml clean package
COPY ./core/target/garden-manager-core-1.0.0-SNAPSHOT.jar /opt/app/app.jar

FROM eclipse-temurin:17
RUN mkdir app
WORKDIR /app
COPY --from=build /opt/app/app.jar ./

EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]