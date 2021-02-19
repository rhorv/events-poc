FROM java:8-jdk-alpine
COPY . /srv
WORKDIR /srv
CMD ["java", "-jar", "/srv/target/gs-maven-0.1.0.jar"]
