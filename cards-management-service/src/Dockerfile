# Stage 1: Build the application
FROM amazoncorretto:21 AS build

# Set the working directory
WORKDIR /app

# Copy the Maven project files for this submodule
COPY pom.xml .
COPY src ./src

# Build the JAR
RUN ./mvnw clean package -pl cards-management-service -am

# Stage 2: Create the runtime image
FROM amazoncorretto:21 AS runtime

# Set the working directory
WORKDIR /app

# Copy the built artifact from the build stage
COPY --from=build ./cards-management-service/target/cards-management-service-0.1.jar ./app.jar

# Expose the application port
EXPOSE 8072

# Run the application
ENTRYPOINT ["java", "-jar", "./app.jar"]