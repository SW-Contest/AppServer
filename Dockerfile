FROM amazoncorretto:17-alpine-jdk
MAINTAINER seungsu <h970126@gmail.com<
LABEL authors="seungsu"

ARG JAR_FILE_PATH=./build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar
RUN apk add tzdata

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
