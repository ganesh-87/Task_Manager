# Use OpenJDK 17 as the base image
FROM eclipse-temurin:17-jdk

# Set working directory inside the container
WORKDIR /app

# Copy JAR file into the container
COPY target/*.jar app.jar

# Expose the port that Render will use
ENV PORT=8080
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "app.jar"]
