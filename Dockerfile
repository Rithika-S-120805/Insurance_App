# FROM eclipse-temurin:26
# COPY . /app
# WORKDIR /app
# RUN .\mvnw clean package -DskipTests
# CMD ["java", "-jar", "target/pos-0.0.1-SNAPSHOT.jar"]

FROM eclipse-temurin:26

WORKDIR /app

COPY . .

# Give execute permission to mvnw (important in Linux)
RUN chmod +x mvnw

# Use Linux-style command
RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/pos-0.0.1-SNAPSHOT.jar"]