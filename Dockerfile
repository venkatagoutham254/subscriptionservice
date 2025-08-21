FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/app.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
