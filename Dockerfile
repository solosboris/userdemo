# Multi-stage build for optimal image size
FROM eclipse-temurin:21-jdk-jammy as builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy

# Install wait-for-it.sh dependencies
RUN apt-get update && apt-get install -y --no-install-recommends postgresql-client netcat && rm -rf /var/lib/apt/lists/*

# Create app user
RUN groupadd -r spring && useradd -r -g spring spring

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/user-management-api-1.0.0.jar app.jar

# Copy wait-for-it script
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Change to non-root user
USER spring

# Expose port
EXPOSE 8080

# Set JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Entry point with database wait
ENTRYPOINT ["/bin/bash", "-c", "/wait-for-it.sh $DB_HOST:5432 --timeout=60 -- java $JAVA_OPTS -jar app.jar"]