# Use a base image with Java 17 (required for Spring Boot 3.5.0)
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the project
COPY src ./src

# Build the project
RUN ./mvnw package -DskipTests

# Run the jar file
CMD ["java", "-jar", "target/mfg-0.0.1-SNAPSHOT.jar"]
