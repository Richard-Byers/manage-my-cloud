FROM maven:3.8.4-openjdk-17-slim AS build

EXPOSE 8080

COPY pom.xml ./authorisation-service/pom.xml

COPY src ./authorisation-service/src

WORKDIR /authorisation-service

RUN mvn clean package -DskipTests

FROM openjdk:17-slim

COPY --from=build authorisation-service/target/*.jar /app.jar

# Set the startup command to run your binary
CMD ["java", "-jar", "/app.jar"]