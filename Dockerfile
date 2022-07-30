FROM openjdk:11-jre-slim-buster
ARG JAR_FILE=target/*.jar
RUN mkdir /server
COPY ${JAR_FILE} /server/jw-home-websocket-server.jar
ENTRYPOINT java -jar /server/jw-home-websocket-server.jar
EXPOSE 9093
