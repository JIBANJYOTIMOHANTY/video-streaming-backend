# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: JRE runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy JAR files from build stage
COPY --from=build /app/eureka-server/target/*.jar eureka-server.jar
COPY --from=build /app/api-gateway/target/*.jar api-gateway.jar
COPY --from=build /app/auth-service/target/*.jar auth-service.jar
COPY --from=build /app/user-service/target/*.jar user-service.jar
COPY --from=build /app/video-service/target/*.jar video-service.jar
COPY --from=build /app/upload-service/target/*.jar upload-service.jar
COPY --from=build /app/streaming-service/target/*.jar streaming-service.jar
COPY --from=build /app/analytics-service/target/*.jar analytics-service.jar

# Copy entrypoint script
COPY entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh

# Expose API Gateway port
EXPOSE 8082

ENTRYPOINT ["./entrypoint.sh"]
