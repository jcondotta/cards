# Use Amazon Corretto (OpenJDK) as a base image
FROM amazoncorretto:21-alpine

# Install curl so that health check commands work
RUN apk --no-cache add curl

# Set a working directory (optional)
WORKDIR /app

# Copy the built JAR from the target directory into the container
# Adjust JAR name as necessary
COPY target/query-service-0.1.jar app.jar

# Expose the Micronaut server port
EXPOSE 8073

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]