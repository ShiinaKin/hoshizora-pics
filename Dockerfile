FROM node:alpine AS node_base

FROM eclipse-temurin:21-alpine AS build

COPY --from=node_base /usr/local/bin/node /usr/local/bin/
COPY --from=node_base /usr/local/lib/node_modules /usr/local/lib/node_modules

WORKDIR /hoshizora

COPY gradlew gradle.properties build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY app app
COPY ui ui

RUN chmod +x ./gradlew
RUN ./gradlew build

FROM eclipse-temurin:21-alpine
LABEL maintainer="ShiinaKin <shiina@sakurasou.io>"
WORKDIR /hoshizora

COPY --from=build /hoshizora/build/*.jar application.jar

VOLUME /hoshizora/uploads
VOLUME /hoshizora/thumbnails

ENV JVM_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT java $JVM_OPTS -jar application.jar