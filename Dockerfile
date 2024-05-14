FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/BankingServerDemo-1.0.0-RELEASE.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]