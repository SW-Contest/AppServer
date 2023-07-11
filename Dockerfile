FROM gradle:7.6-jdk17-alpine as builder

ENV APP_HOME=/apps
WORKDIR $APP_HOME

COPY gradlew $APP_HOME
COPY gradle $APP_HOME/gradle
COPY src $APP_HOME/src
COPY build.gradle $APP_HOME
COPY settings.gradle $APP_HOME

RUN chmod +x ./gradlew
RUN ./gradlew clean bootjar

FROM openjdk:17-alpine

ENV APP_HOME=/apps
WORKDIR $APP_HOME

ARG JAR_FILE_PATH=./build/libs/*.jar
COPY --from=builder $APP_HOME/${JAR_FILE_PATH} app.jar

EXPOSE 80
ENTRYPOINT ["java", "-jar", "app.jar"]
