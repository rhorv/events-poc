FROM java:8-jdk-alpine
COPY ./target/* /srv/
WORKDIR /srv
CMD ["java", "-jar", "/srv/eda-poc-0.1.0.jar"]
