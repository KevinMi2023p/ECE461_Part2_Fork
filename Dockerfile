# Use the official maven/Java 17 image to create a build artifact.
# https://hub.docker.com/_/maven
FROM maven:3.8.3-openjdk-17-slim AS build

# Install necessary dependencies for building the native library
RUN apt-get update && apt-get install -y \
    build-essential \
    g++ \
    gcc \
    git \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . /app

# Build golib
RUN cd cli && go build -o libpackageanalyze.so -buildmode=c-shared main.go && cd ..

# Build jheader
RUN javac api_paths/src/main/java/com/spring_rest_api/cli/*.java -h ./cli

# Build clib
RUN g++ -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -shared -o libNetScoreUtil.so cli/com_spring_rest_api_cli_NetScoreUtil.cpp cli/libpackageanalyze.so

# Copy the native libraries to /usr/lib
RUN cp ./cli/libpackageanalyze.so /usr/lib/libpackageanalyze.so
RUN cp ./libNetScoreUtil.so /usr/lib/libNetScoreUtil.so

# Build the Spring Boot application
RUN mvn -f /app/api_paths/pom.xml clean package

# Use AdoptOpenJDK for base image.
# https://hub.docker.com/_/adoptopenjdk
FROM eclipse-temurin:17-jdk-alpine

# Define the API_KEY build-time substitution variable
ARG API_KEY

# Set the API_KEY environment variable
ENV API_KEY=${API_KEY}

# Copy the native libraries from the build stage
COPY --from=build /usr/lib/libpackageanalyze.so /usr/lib/libpackageanalyze.so
COPY --from=build /usr/lib/libNetScoreUtil.so /usr/lib/libNetScoreUtil.so

# Copy the jar to the production image from the build stage.
COPY --from=build /app/api_paths/target/ece461-part2.jar /app/app.jar
COPY --from=build /app/accountKey.json /app/accountKey.json

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/accountKey.json

# Run the web service on container startup.
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
