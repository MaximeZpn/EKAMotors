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

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && apt-get clean

# Set working directory
WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /app/target/market-MS-1.0-SNAPSHOT.jar market-MS.jar

# Expose port
EXPOSE 8083

# Run the application
CMD ["java", "-jar", "market-MS.jar"]