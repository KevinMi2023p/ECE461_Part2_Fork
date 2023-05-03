# Use the official maven/Java 17 image to create a build artifact.
FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /app
COPY . /app

# COPY ./cli/libpackageanalyze.so /usr/lib/libpackageanalyze.so
# COPY ./libNetScoreUtil.so /usr/lib/libNetScoreUtil.so



RUN mvn -f /app/api_paths/pom.xml clean package

# Use an Ubuntu base image that includes glibc and other necessary tools
FROM ubuntu:20.04

# Define the API_KEY build-time substitution variable
ARG API_KEY

# Set the API_KEY environment variable
ENV API_KEY=${API_KEY}

# Install necessary dependencies and OpenJDK 17
# RUN apt-get update && \
#     apt-get install -y openjdk-17-jdk && \
#     rm -rf /var/lib/apt/lists/*

RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        openjdk-17-jdk \
        maven \
        build-essential \
        wget && \
    rm -rf /var/lib/apt/lists/*

# Setup Process
RUN wget https://go.dev/dl/go1.20.3.linux-amd64.tar.gz && \
    rm -rf /usr/local/go && tar -C /usr/local -xzf go1.20.3.linux-amd64.tar.gz



ENV PATH="/usr/local/go/bin:${PATH}"
ENV GOROOT="/usr/local/go"
ENV GOPATH="$HOME/go"
ENV PATH="$GOPATH/bin:$GOROOT/bin:$PATH"
ENV JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"

WORKDIR /app
COPY . /app

# Building lib files
RUN cd cli && \
    go mod tidy && \
    go build -o libpackageanalyze.so -buildmode=c-shared main.go && \
    ls && \
    cp libpackageanalyze.* /usr/lib && \
    cd .. 

RUN javac api_paths/src/main/java/com/spring_rest_api/cli/*.java -h ./cli

RUN g++ -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -shared -o /usr/lib/libNetScoreUtil.so cli/com_spring_rest_api_cli_NetScoreUtil.cpp /usr/lib/libpackageanalyze.so

# Copy the jar to the production image from the build stage.
COPY --from=build /app/api_paths/target/ece461-part2.jar /app/app.jar
COPY --from=build /app/accountKey.json /app/accountKey.json
# COPY --from=build /usr/lib/libpackageanalyze.so /usr/lib/libpackageanalyze.so
# COPY --from=build /usr/lib/libNetScoreUtil.so /usr/lib/libNetScoreUtil.so

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/accountKey.json
# ENV LD_LIBRARY_PATH=/usr/lib

RUN ls /usr/lib && echo "Contents of /usr/lib listed above."
RUN ls /usr/lib/libpackageanalyze.so && ls /usr/lib/libNetScoreUtil.so || echo "Required files not found in /usr/lib directory"

# ENV JAVA_TOOL_OPTIONS -Djava.library.path=/usr/lib

# Run the web service on container startup.
CMD ["bash","docker.bash"]
