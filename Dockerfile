FROM openjdk:17-slim-buster
ARG JAR_FILE=target/*.jar
COPY ./target/Interview-assignment-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]