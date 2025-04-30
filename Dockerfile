# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Create working directory
WORKDIR /app

# Copy everything into the container
COPY . .

# Package the app (skip tests to speed up)
RUN mvn clean package -DskipTests

# Stage 2: Run the app with a smaller Java image
FROM eclipse-temurin:17

# Working directory in the new image
WORKDIR /app

# Copy built JAR from previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (optional but good practice)
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
