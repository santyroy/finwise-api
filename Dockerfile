# Stage 1: Build Stage with Maven
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven build file and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code to the container
COPY src /app/src

# Build the application with Maven
RUN mvn clean package -DskipTests

# Stage 2: Runtime Stage with only OpenJDK
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot application will run on (default is 8080)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
