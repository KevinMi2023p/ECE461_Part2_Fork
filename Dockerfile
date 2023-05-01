# Use the official maven/Java 17 image to create a build artifact.
# https://hub.docker.com/_/maven
FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /app
COPY . /app
# Copy ./cli/libpackageanalyze.so to /usr/lib/



COPY ./cli/libpackageanalyze.so /app
COPY ./libNetScoreUtil.so /app

RUN ls /app

RUN cp /app/libpackageanalyze.so /usr/lib
RUN cp /app/libNetScoreUtil.so /usr/lib

RUN ls /usr/lib && echo "Contents of /usr/lib listed above."

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

ENV LD_LIBRARY_PATH=/usr/lib
RUN cp /app/libpackageanalyze.so /usr/lib
RUN cp /app/libNetScoreUtil.so /usr/lib
RUN ls /usr/lib && echo "Contents of /usr/lib listed above."
RUN ls /usr/lib/libpackageanalyze.so && ls /usr/lib/libNetScoreUtil.so || echo "Required files not found in /usr/lib directory"

ENV JAVA_TOOL_OPTIONS -Djava.library.path=/usr/lib

# Run the web service on container startup.
CMD ["java", "-Djava.security.egd=file:/dev/./urandom",  "-jar", "/app/app.jar","-Djava.library.path=/usr/lib"]