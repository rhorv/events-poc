FROM java:8-jdk-alpine
COPY ./target/* /srv/
COPY ./bin/run.sh /srv/run.sh
WORKDIR /srv
CMD ["/bin/sh", "/srv/run.sh"]
