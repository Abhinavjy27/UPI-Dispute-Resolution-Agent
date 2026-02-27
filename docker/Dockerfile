# ============================================
# Backend Dockerfile - Multi-Stage Build
# ============================================
# Build stage: mvn clean package
# Runtime stage: openjdk:17-jdk-slim

# ============ STAGE 1: BUILD ============
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy pom.xml first (for better layer caching)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests -q

# ============ STAGE 2: RUNTIME ============
FROM openjdk:17-jdk-slim

WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy JAR from builder
COPY --from=builder /app/target/dispute-api-*.jar app.jar

# Try alternate name if above fails
RUN if [ ! -f app.jar ]; then \
    cp /app/target/*.jar app.jar 2>/dev/null || \
    echo "No JAR file found"; \
    fi

# Expose port
EXPOSE 8000

# Health check
HEALTHCHECK --interval=10s --timeout=5s --retries=5 --start-period=30s \
    CMD curl -f http://localhost:8000/api/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]

# ============ CONFIGURATION ============
# BUILD:
#   docker build -t disputes-backend:latest .
#
# RUN:
#   docker run -p 8000:8000 --env-file .env.dev disputes-backend:latest
#
# WITH ENVIRONMENT:
#   docker run -p 8000:8000 \
#     -e SERVER_PORT=8000 \
#     -e DATABASE_URL=jdbc:sqlite:disputes.db \
#     disputes-backend:latest
#
# DEBUGGING (Interactive):
#   docker run -it -p 8000:8000 --env-file .env.dev disputes-backend:latest bash
