FROM openjdk:17-jdk
COPY target/ocitaskrestservice-1.0-SNAPSHOT.jar ocitaskrestservice-1.0.0.jar
COPY ocitaskrestservice.yml ocitaskrestservice.yml
ENTRYPOINT ["java","-jar","/ocitaskrestservice-1.0.0.jar","server","/ocitaskrestservice.yml"]
