FROM eclipse-temurin:21 AS build

WORKDIR /hoshizora-pics

COPY gradlew gradle.properties build.gradle.kts settings.gradle.kts ./
COPY .git .git
COPY gradle gradle
COPY app app
COPY ui ui

RUN chmod +x ./gradlew
RUN ./gradlew clean build

FROM eclipse-temurin:21-alpine
LABEL maintainer="ShiinaKin <shiina@sakurasou.io>"
WORKDIR /hoshizora-pics

COPY --from=build /hoshizora-pics/build/*.jar hoshizora-pics.jar

VOLUME /hoshizora-pics/images

ENV JVM_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar hoshizora-pics.jar"]
