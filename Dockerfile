FROM openjdk:8-alpine

ADD . .
RUN ["./gradlew", "build"]
