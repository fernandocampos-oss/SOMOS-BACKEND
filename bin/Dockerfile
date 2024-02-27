FROM openjdk:11-jdk-slim-buster

#Gradle: build/libs/my-app.jar
#Maven: target/my-app.jar
ARG JAR_FILE

ENV APP_HOME=/app
ENV APP_JAR_NAME=app.jar
ENV APP_JAR_FILE=$APP_HOME/$APP_JAR_NAME

WORKDIR ${APP_HOME}

COPY ${JAR_FILE} ${APP_JAR_FILE}

ENTRYPOINT java -jar $APP_JAR_NAME