# Multi-stage Dockerfile for Spring Boot + React deployment

# Stage 1: Build Frontend
FROM node:20-alpine AS frontend-builder

ARG FRONTEND_REPO_URL=https://github.com/Aditya2434/ZenBill-FE.git
ARG FRONTEND_BRANCH=main

WORKDIR /frontend

# Clone and build frontend
RUN apk add --no-cache git && \
    git clone -b ${FRONTEND_BRANCH} ${FRONTEND_REPO_URL} . && \
    npm install && \
    npm run build

# Stage 2: Build Backend
FROM maven:3.9-eclipse-temurin-20 AS backend-builder

WORKDIR /app

# Copy backend source
COPY pom.xml .
COPY src ./src

# Copy frontend build to static resources
COPY --from=frontend-builder /frontend/dist ./src/main/resources/static

# Build Spring Boot application
RUN mvn clean package -DskipTests

# Stage 3: Runtime
FROM eclipse-temurin:20-jre-alpine

WORKDIR /app

# Copy the built JAR from backend-builder stage
COPY --from=backend-builder /app/target/invoice-management-system-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

