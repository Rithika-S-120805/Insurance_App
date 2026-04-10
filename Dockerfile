FROM eclipse-temurin-26
COPY . /app
WORKDIR /app
RUN mvn clean package
CMD ["java", "-jar", "target/my-app.jar"]
