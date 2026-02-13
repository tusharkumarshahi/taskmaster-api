# ============================================
# Stage 1: Build the application
# ============================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy project files
COPY pom.xml .
COPY src src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# ============================================
# Stage 2: Runtime image
# ============================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
