# Use OpenJDK 17 as the base image
FROM eclipse-temurin:17-jdk AS build

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and project files
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Use a smaller JDK image for runtime
FROM eclipse-temurin:17-jdk

# Set the working directory
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port (Render will use this)
ENV PORT=8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
