# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy POM file and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /app/target/auth-MS-1.0-SNAPSHOT.jar auth-MS.jar

# Expose port
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "auth-MS.jar"]