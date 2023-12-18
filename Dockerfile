# syntax=docker/dockerfile:1
FROM maven:3.9.4-eclipse-temurin-17-alpine as build

ARG DATABASE_URL
ARG DATABASE_USERNAME
ARG DATABASE_PASSWORD
ARG TEST_DATABASE_URL
ARG TEST_DATABASE_USERNAME
ARG TEST_DATABASE_PASSWORD

LABEL authors="kodacci"

COPY ./ /opt/src
WORKDIR /opt/src

ENV DATABASE_URL="$DATABASE_URL"
ENV DATABASE_USERNAME="$DATABASE_USERNAME"
ENV DATABASE_PASSWORD="$DATABASE_PASSWORD"
ENV TEST_DATABASE_URL="$TEST_DATABASE_URL"
ENV TEST_DATABASE_USERNAME="$TEST_DATABASE_USERNAME"
ENV TEST_DATABASE_PASSWORD="$TEST_DATABASE_PASSWORD"

RUN mvn -f pom.xml -Dskip.jooq.generation=true -DskipTest clean package
COPY ./core/target/garden-manager-core-1.0.0-SNAPSHOT.jar /opt/app/app.jar

FROM eclipse-temurin:17
RUN mkdir app
WORKDIR /app
COPY --from=build /opt/app/app.jar ./

EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]