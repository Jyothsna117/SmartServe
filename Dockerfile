# ==============================================================================
# SmartServe Multi-Stage Dockerfile
# Stage 1: Build & Package with Maven and Java 21 JDK
# Stage 2: Minimalist Lightweight Production Runtime with Eclipse Temurin 21 JRE
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build Stage
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and POM first to leverage Docker layer caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Download dependencies offline (cached layer)
# If offline flag fails in isolated environments, fall back seamlessly
RUN ./mvnw dependency:go-offline -B || true

# Copy application source code
COPY src src

# Run tests and compile executable fat JAR
RUN ./mvnw clean package -DskipTests

# ------------------------------------------------------------------------------
# Stage 2: Runtime Stage
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="SmartServe DevOps Engineering Team"
LABEL project="SmartServe - Real-Time Complaint and Service Management System"
LABEL version="1.0.0"

WORKDIR /app

# Create dedicated non-root application user and group for container security
RUN addgroup -S smartserve && adduser -S smartserve -G smartserve

# Copy compiled JAR artifact from builder stage
COPY --from=builder /app/target/smartserve-1.0.0.jar /app/smartserve.jar

# Adjust ownership
RUN chown -R smartserve:smartserve /app

# Switch to unprivileged security user
USER smartserve:smartserve

# Expose standard Spring Boot HTTP application port
EXPOSE 8080

# Environment variables with container defaults
# Note: For Docker on Windows host, host.docker.internal connects back to the host MongoDB service
ENV SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/smartserve_db
ENV SMARTSERVE_ADMIN_USER=admin
ENV SMARTSERVE_ADMIN_PASSWORD=SmartServe@2026Admin
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

# Container Healthcheck using Spring Boot Actuator probe
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Launch application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/smartserve.jar"]
