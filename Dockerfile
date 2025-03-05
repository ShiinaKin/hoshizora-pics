FROM eclipse-temurin:21-jdk AS build

WORKDIR /hoshizora-pics

COPY gradlew gradle.properties build.gradle.kts settings.gradle.kts ./
COPY .git .git
COPY gradle gradle
COPY app app
COPY ui ui

RUN chmod +x ./gradlew
RUN ./gradlew clean build

FROM eclipse-temurin:21-jre-noble
LABEL maintainer="ShiinaKin <shiina@sakurasou.io>"
WORKDIR /hoshizora-pics

# ImageMagic 6.x
RUN apt update && apt install --no-install-recommends imagemagick -y

COPY --from=build /hoshizora-pics/build/*.jar hoshizora-pics.jar

VOLUME /hoshizora-pics/images

ENV JVM_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["bash", "-c", "java $JVM_OPTS -jar hoshizora-pics.jar"]
