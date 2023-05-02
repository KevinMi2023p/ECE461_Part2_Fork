# Use the official maven/Java 17 image to create a build artifact.
FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /app
COPY . /app

# Add the GitHub token and account key to the image
RUN echo "$API_KEY" > api_paths/src/main/resources/githubToken.txt
RUN echo "$ACCOUNT_KEY" | base64 -d > accountKey.json

# Build the Maven project
RUN mvn -f /app/api_paths/pom.xml clean package

# Use an Ubuntu base image that includes glibc and other necessary tools
FROM ubuntu:20.04

# Set the API_KEY environment variable
ENV API_KEY=${API_KEY}

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

# Copy the account key and GitHub token to the production image
COPY --from=build /app/accountKey.json /app/githubToken.txt /app/

# Build the C++ library and copy it to /usr/lib
COPY ./cli /app/cli
RUN cd /app/cli && \
    go mod tidy && \
    go build -o libpackageanalyze.so -buildmode=c-shared main.go && \
    javac com/spring_rest_api/cli/*.java -h ./ && \
    g++ -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -shared -o /usr/lib/libNetScoreUtil.so com_spring_rest_api_cli_NetScoreUtil.cpp /app/cli/libpackageanalyze.so

# Copy the jar to the production image from the build stage.
COPY --from=build /app/api_paths/target/ece461-part2.jar /app/app.jar

# Following Install jq for json verification
RUN apt-get update && \
    apt-get install -y jq && \
    rm -rf /var/lib/apt/lists/*

# Set the GOOGLE_APPLICATION_CREDENTIALS environment variable
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/accountKey.json

# Run the web service on container startup.
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
