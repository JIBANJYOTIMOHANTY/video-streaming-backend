#!/bin/bash

# Port mapping. Render passes PORT to bind to the outer web service.
export PORT=${PORT:-8082}
export EUREKA_SERVER_URL=${EUREKA_SERVER_URL:-http://localhost:8761/eureka/}

# Optimized JVM flags for running in very low RAM (512MB total)
JVM_OPTS="-Xms16m -Xmx32m -XX:TieredStopAtLevel=1 -XX:ReservedCodeCacheSize=12m -Xss256k -Dspring.main.lazy-initialization=true -Dspring.backgroundpreinitializer.ignore=true"

# Optimized gateway flag (needs slightly more heap for load-balancing logic)
GATEWAY_OPTS="-Xms20m -Xmx40m -XX:TieredStopAtLevel=1 -XX:ReservedCodeCacheSize=12m -Xss256k -Dspring.main.lazy-initialization=true -Dspring.backgroundpreinitializer.ignore=true"

echo "============================================="
echo "Starting Eureka Server..."
echo "============================================="
java $JVM_OPTS -jar eureka-server.jar &

# Wait for Eureka to be up
sleep 12

echo "============================================="
echo "Starting API Gateway on port $PORT..."
echo "============================================="
java $GATEWAY_OPTS -jar api-gateway.jar --server.port=$PORT &

# Wait a bit for Gateway to bind
sleep 5

echo "============================================="
echo "Starting Microservices..."
echo "============================================="
java $JVM_OPTS -jar auth-service.jar &
java $JVM_OPTS -jar user-service.jar &
java $JVM_OPTS -jar video-service.jar &
java $JVM_OPTS -jar upload-service.jar &
java $JVM_OPTS -jar streaming-service.jar &
java $JVM_OPTS -jar analytics-service.jar &

# Keep the shell container alive
wait -n
