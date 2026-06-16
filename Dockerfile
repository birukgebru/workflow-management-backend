# =========================
# 1. Build Stage (Maven)
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first (for dependency caching)
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (creates JAR in target/)
RUN mvn clean package -DskipTests


# =========================
# 2. Runtime Stage (JAR only)
# =========================
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy built JAR from previous stage
COPY --from=build /app/target/*.jar app.jar

# Railway provides PORT dynamically
ENV PORT=8080

EXPOSE 8080

# Run application
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]