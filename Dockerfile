#FROM gradle:7.2.0-jdk8-openj9 AS build
#COPY --chown=gradle:gradle . /home/gradle/src
#WORKDIR /home/gradle/src
#RUN gradle build --no-daemon

FROM openjdk:8-alpine

ADD . .
RUN ["./gradlew", "build"]
