FROM openjdk:8-jdk-alpine

VOLUME /tmp
WORKDIR /app

COPY ./target/word-count-1.0-SNAPSHOT-jar-with-dependencies.jar /app/wordcount.jar

RUN apk update && apk add --no-cache libc6-compat

RUN sh -c 'touch /app/wordcount.jar'

CMD java -jar wordcount.jar