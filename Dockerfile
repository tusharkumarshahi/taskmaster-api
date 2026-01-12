FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ARG JAR_FILE=target/taskmaster-*.jar
COPY ${JAR_FILE} app.jar

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
