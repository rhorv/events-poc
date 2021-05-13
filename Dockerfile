FROM java:8-jdk-alpine
COPY ./target/* /srv/
WORKDIR /srv
CMD ["/bin/bash", "/srv/bin/run.sh"]
