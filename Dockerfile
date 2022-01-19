FROM gradle:7.3.3-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17

RUN mkdir /app

COPY --from=build /home/gradle/src/server/build/libs /app/

ENTRYPOINT ["java","-jar","/app/server.jar"]
