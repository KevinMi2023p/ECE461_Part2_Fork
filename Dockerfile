# Use the official maven/Java 17 image to create a build artifact.
# https://hub.docker.com/_/maven
FROM maven:3.8.3-openjdk-17-slim AS build

WORKDIR /app
COPY . /app

# Build native library
RUN apt-get update && apt-get install -y gcc
RUN gcc -shared -o libpackageanalyze.so -fPIC -I/usr/lib/jvm/java-11-openjdk-amd64/include -I/usr/lib/jvm/java-11-openjdk-amd64/include/linux ./cli/libpackageanalyze.c
RUN gcc -shared -o libNetScoreUtil.so -fPIC -I/usr/lib/jvm/java-11-openjdk-amd64/include -I/usr/lib/jvm/java-11-openjdk-amd64/include/linux ./cli/libNetScoreUtil.c

# Copy native libraries to the library path
RUN mkdir -p /usr/local/lib
RUN cp /app/libpackageanalyze.so /usr/local/lib/libpackageanalyze.so
RUN cp /app/libNetScoreUtil.so /usr/local/lib/libNetScoreUtil.so

# Build the project with Maven
RUN mvn -f /app/api_paths/pom.xml clean package

# Use AdoptOpenJDK for base image.
# https://hub.docker.com/_/adoptopenjdk
FROM eclipse-temurin:17-jdk-alpine

# Define the API_KEY build-time substitution variable
ARG API_KEY

# Set the API_KEY environment variable
ENV API_KEY=${API_KEY}

# Copy the jar to the production image from the build stage.
COPY --from=build /app/api_paths/target/ece461-part2.jar /app/app.jar
COPY --from=build /app/accountKey.json /app/accountKey.json

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/accountKey.json

# Set the library path
ENV LD_LIBRARY_PATH=/usr/local/lib

# Run the web service on container startup.
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
