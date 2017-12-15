FROM openjdk:8
VOLUME /tmp
ADD ./target/users-0.0.1-SNAPSHOT.jar /users.jar
RUN sh -c 'touch /users.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/users.jar"]
