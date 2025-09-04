FROM openjdk:21-jdk-slim
WORKDIR /app
COPY app.jar app.jar
EXPOSE 8084
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
HEALTHCHECK --interval=15s --timeout=5s --retries=10 \
  CMD curl -fsS http://localhost:8084/actuator/health/liveness || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
