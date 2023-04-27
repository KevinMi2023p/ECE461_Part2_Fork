ARG API_KEY_ARG

# Use the official maven/Java 17 image to create a build artifact.
# https://hub.docker.com/_/maven
FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /app
COPY . /app

# Set the API_KEY environment variable
ENV API_KEY=$API_KEY_ARG

# Copy ./libNetScoreUtil.so to /usr/lib/
COPY /app/libNetScoreUtil.so /usr/lib/libNetScoreUtil.so

# Use AdoptOpenJDK for base image.
# https://hub.docker.com/_/adoptopenjdk
FROM openjdk:17-alpine
RUN mvn -f /app/api_paths/pom.xml clean package
# Copy the jar to the production image from the build stage.
COPY --from=build /app/api_paths/target/*.jar /app/app.jar

# Run the web service on container startup.
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
