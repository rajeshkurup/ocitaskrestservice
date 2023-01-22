FROM openjdk:17-jdk
WORKDIR /
ADD target/ocitaskrestservice-1.0-SNAPSHOT.jar ocitaskrestservice-1.0.0.jar
ADD ocitaskrestservice-local.yml ocitaskrestservice.yml
EXPOSE 8080 8081
ENTRYPOINT ["java","-jar","ocitaskrestservice-1.0.0.jar","server","ocitaskrestservice.yml"]
