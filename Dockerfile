# Use the official maven/Java 17 image to create a build artifact.
# https://hub.docker.com/_/maven
FROM adoptopenjdk:17-jdk-hotspot-focal AS build
WORKDIR /app
COPY . /app

COPY ./cli/libpackageanalyze.so /usr/lib/libpackageanalyze.so
COPY ./libNetScoreUtil.so /usr/lib/libNetScoreUtil.so

RUN mvn -f /app/api_paths/pom.xml clean package

# Use a base image that includes glibc
FROM frolvlad/alpine-glibc:latest

# Define the API_KEY build-time substitution variable
ARG API_KEY

# Set the API_KEY environment variable
ENV API_KEY=${API_KEY}

# Copy the jar to the production image from the build stage.
COPY --from=build /app/api_paths/target/ece461-part2.jar /app/app.jar
COPY --from=build /app/accountKey.json /app/accountKey.json
COPY --from=build /usr/lib/libpackageanalyze.so /usr/lib/libpackageanalyze.so
COPY --from=build /usr/lib/libNetScoreUtil.so /usr/lib/libNetScoreUtil.so

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/accountKey.json
ENV LD_LIBRARY_PATH=/usr/lib

RUN ls /usr/lib && echo "Contents of /usr/lib listed above."
RUN ls /usr/lib/libpackageanalyze.so && ls /usr/lib/libNetScoreUtil.so || echo "Required files not found in /usr/lib directory"

ENV JAVA_TOOL_OPTIONS -Djava.library.path=/usr/lib

# Run the web service on container startup.
CMD ["java", "-Djava.security.egd=file:/dev/./urandom",  "-jar", "/app/app.jar"]
