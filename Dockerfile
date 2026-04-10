FROM eclipse-temurin:25
COPY . /app
WORKDIR /app
RUN mvn clean package
CMD ["java", "-jar", "target/my-app.jar"]
