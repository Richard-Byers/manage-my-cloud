# Use an official Maven/Java runtime as a parent image
FROM maven:3.8.4-openjdk-17-slim AS builder

# Set the working directory to /app
WORKDIR /app

# Copy the parent POM file
COPY pom.xml /app/pom.xml

# Copy the entire project
COPY . /app/

# Build the entire project
RUN mvn clean install -DskipTests

# Create a new image with only the necessary artifacts
FROM openjdk:17-slim
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/authorisation-service/target/*.jar /app/authorisation-service.jar

# Define the command to run your application
CMD ["java", "-jar", "authorisation-service.jar"]
