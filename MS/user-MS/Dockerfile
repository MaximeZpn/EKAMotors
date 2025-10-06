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
COPY --from=build /app/target/user-MS-1.0-SNAPSHOT.jar user-MS.jar

# Expose port
EXPOSE 8082

# Run the application
CMD ["java", "-jar", "user-MS.jar"]