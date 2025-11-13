#!/bin/bash

echo "Building JAR with Maven..."
mvn clean package -DskipTests

echo "Copying JAR to app.jar for Docker compatibility..."
cp target/app.jar app.jar

echo "Starting Docker deployment..."
docker-compose down
docker-compose up --build -d

echo "Waiting for application to start..."
sleep 30

echo "Checking application health..."
curl -f http://localhost:8084/api/health

echo "Deployment complete!"
