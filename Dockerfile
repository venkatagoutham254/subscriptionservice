FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/susbcriptionservice-0.0.1-SNAPSHOT.jar subscriptionservice.jar
ENTRYPOINT ["java", "-jar", "subscriptionservice.jar"]
