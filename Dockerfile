## Build stage
FROM gradle:jdk21-jammy AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

## Package stage
FROM openjdk:21-jdk-slim

ENV port 9200
ENV datasource.url 'jdbc:postgresql://host.docker.internal:5432/tgBank'
ENV datasource.username 'pg'
ENV datasource.password 'pg'
ENV redis.host 'host.docker.internal'
ENV redis.port '6379'
ENV redis.password 'my-password'

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/tgBank-1.0.0.jar
ENTRYPOINT ["java","-jar","/app/tgBank-1.0.0.jar"]