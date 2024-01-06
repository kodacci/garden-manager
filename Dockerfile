# syntax=docker/dockerfile:1
FROM maven:3.9.4-eclipse-temurin-17-alpine as build

LABEL authors="Andrey Ryabtsev"

COPY ./ /opt/src
WORKDIR /opt/src

RUN mvn -f pom.xml -Dskip.jooq.generation=true -Dskip.unit.tests -DskipTests clean package
COPY ./core/target/garden-manager-core-app.jar /opt/app/app.jar

FROM eclipse-temurin:17
RUN mkdir app
WORKDIR /app
COPY --from=build /opt/app/app.jar ./

EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]