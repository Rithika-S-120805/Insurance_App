FROM eclipse-temurin:26
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests
CMD ["java", "-jar", "target/my-app.jar"]
