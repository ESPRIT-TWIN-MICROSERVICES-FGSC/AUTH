FROM openjdk:17
EXPOSE 5050
ADD /target/auth-0.0.1-SNAPSHOT.jar docker-client.jar
ENTRYPOINT ["java","-jar","docker-client.jar"]
