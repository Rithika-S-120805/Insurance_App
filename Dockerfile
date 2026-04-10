# ---------- Stage 1: Build ----------
FROM maven:3.14.1-eclipse-temurin-26 AS builder
WORKDIR /app

# Copy pom and download dependencies (cache optimization)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:26-jdk
WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Render uses dynamic PORT
ENV PORT=8080
EXPOSE 8080

# Run app on Render port
ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]