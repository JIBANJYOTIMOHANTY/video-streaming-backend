#!/bin/bash

# Port mapping. Render passes PORT to bind to the outer web service.
export PORT=${PORT:-8082}
export EUREKA_SERVER_URL=${EUREKA_SERVER_URL:-http://localhost:8761/eureka/}

echo "============================================="
echo "Starting Eureka Server..."
echo "============================================="
java -jar eureka-server.jar &

# Wait for Eureka to be up
sleep 12

echo "============================================="
echo "Starting API Gateway on port $PORT..."
echo "============================================="
java -jar api-gateway.jar --server.port=$PORT &

# Wait a bit for Gateway to bind
sleep 5

echo "============================================="
echo "Starting Microservices..."
echo "============================================="
java -jar auth-service.jar &
java -jar user-service.jar &
java -jar video-service.jar &
java -jar upload-service.jar &
java -jar streaming-service.jar &
java -jar analytics-service.jar &

# Keep the shell container alive
wait -n
