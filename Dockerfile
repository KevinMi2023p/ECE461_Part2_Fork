# Use the official maven/Java 17 image to create a build artifact.
# https://hub.docker.com/_/maven
FROM maven:3.8.3-openjdk-17-slim AS build

# Copy the native libraries to the build stage
COPY ./cli/libpackageanalyze.so /usr/lib/libpackageanalyze.so
COPY ./libNetScoreUtil.so /usr/lib/libNetScoreUtil.so

# Set the working directory and copy your project files
WORKDIR /app
COPY . /app

# Build the project with Maven
RUN mvn -f /app/pom.xml clean package

# Use AdoptOpenJDK for the base image.
# https://hub.docker.com/_/adoptopenjdk
FROM eclipse-temurin:17-jdk-alpine

# Define the API_KEY build-time substitution variable
ARG API_KEY

# Set the API_KEY environment variable
ENV API_KEY=${API_KEY}

# Expose the port your Spring Boot app is running on
EXPOSE 8080

# Copy the jar to the production image from the build stage.
COPY --from=build /app/target/*.jar /app/app.jar

# Run the web service on container startup.
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
