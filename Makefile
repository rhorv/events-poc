SHELL := /bin/bash

build:
	mvn compile && mvn package

check-deps:
	@printf "checking for java ... ";
	@if [ "`which java`" != "" ]; then echo "found"; else echo "missing"; fi

	@printf "checking for maven ... ";
	@if [ "`which mvn`" != "" ]; then echo "found"; else echo "missing"; fi

	@printf "checking for docker ... ";
	@if [ "`which docker`" != "" ]; then echo "found"; else echo "missing"; fi

	@printf "checking for docker-compose ... ";
	@if [ "`which docker-compose`" != "" ]; then echo "found"; else echo "missing"; fi

	@printf "checking for docker permissions ... ";
	@if [ "`docker ps | grep 'CONTAINER'`" != "" ]; then echo "pass"; else echo "fail"; fi

init: docker-init

docker-init:
	@if [ "`docker network ls | grep poc-events`" == "" ]; then docker network create poc-events; else echo "Docker network already exists"; fi
	mkdir -p `grep 'RABBITMQ_HOME' docker/rabbitmq/.env | cut -d '=' -f 2`
	mkdir -p `grep 'KAFKA_HOME' docker/kafka/.env | cut -d "=" -f 2`;
	mkdir -p `grep 'ELK_HOME' docker/elk/.env | cut -d "=" -f 2`/elasticsearch-data;

rabbitmq-setup:
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare queue name=testqueue durable=true
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare queue name=logging durable=true
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare exchange name=events type=fanout
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare exchange name=commands type=fanout
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare binding source=events destination=testqueue destination_type=queue
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare binding source=events destination=logging destination_type=queue
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare binding source=commands destination=testqueue destination_type=queue
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare binding source=commands destination=logging destination_type=queue

kafka-setup:
	docker exec -it kafka /bin/bash /opt/kafka/bin/kafka-topics.sh --create --if-not-exists --topic testtopic --bootstrap-server localhost:9092
	docker exec -it kafka /bin/bash /opt/kafka/bin/kafka-topics.sh --create --if-not-exists --topic othertopic --bootstrap-server localhost:9092

rabbitmq-start:
	@export PWD=`pwd`
	@cd ${PWD}/docker/rabbitmq; docker-compose up -d
	@cd ${PWD}

rabbitmq-stop:
	@export PWD=`pwd`
	@cd ${PWD}/docker/rabbitmq; docker-compose down
	@cd ${PWD}

kafka-start:
	@export PWD=`pwd`
	@cd ${PWD}/docker/kafka; docker-compose up -d
	@cd ${PWD}

kafka-stop:
	@export PWD=`pwd`;
	@cd ${PWD}/docker/kafka; docker-compose down;
	@cd ${PWD}

elk-start:
	@export PWD=`pwd`;
	@cd ${PWD}/docker/elk; docker-compose up -d;
	@cd ${PWD}

elk-stop:
	@export PWD=`pwd`;
	@cd ${PWD}/docker/elk; docker-compose down;
	@cd ${PWD}

start-services: rabbitmq-start kafka-start elk-start

stop-services: elk-stop kafka-stop rabbitmq-stop

test:
	mvn test

container: clean build
	docker build -t events-poc .

clean:
	mvn clean


