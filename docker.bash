#!/bin/bash


cd cli && \
go mod tidy && \
go build -o libpackageanalyze.so -buildmode=c-shared main.go && \
ls && \
cp libpackageanalyze.* /usr/lib && \
cd .. 

javac api_paths/src/main/java/com/spring_rest_api/cli/*.java -h ./cli

g++ -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -shared -o /usr/lib/libNetScoreUtil.so cli/com_spring_rest_api_cli_NetScoreUtil.cpp /usr/lib/libpackageanalyze.so


cat accountKey.json && 
cat api_paths/src/main/resources/githubToken.txt &&
java -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar