SHELL := /bin/bash

build: ## Builds this project [Default]
	mvn compile && mvn package

check-deps: ## Checks the required dependecies
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

init: docker-init ## Initializes the project

docker-init: ## Initializes the docker network adapter and creates required directories
	@if [ "`docker network ls | grep poc-events`" == "" ]; then docker network create poc-events; else echo "Docker network already exists"; fi
	mkdir -p `grep 'RABBITMQ_HOME' docker/rabbitmq/.env | cut -d '=' -f 2`
	mkdir -p `grep 'KAFKA_HOME' docker/kafka/.env | cut -d "=" -f 2`;
	mkdir -p `grep 'ELK_HOME' docker/elk/.env | cut -d "=" -f 2`/elasticsearch-data;

rabbitmq-setup: ## Initializes the rabbitmq queues and exchanges
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare queue name=testqueue durable=true
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare queue name=logging durable=true
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare exchange name=events type=fanout
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare exchange name=commands type=fanout
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare binding source=events destination=testqueue destination_type=queue
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare binding source=events destination=logging destination_type=queue
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare binding source=commands destination=testqueue destination_type=queue
	docker exec -it rabbitmq rabbitmqadmin --username admin --password admin123 declare binding source=commands destination=logging destination_type=queue

kafka-setup: ## Initializes the kafka topics
	docker exec -it kafka /bin/bash /usr/bin/kafka-topics --create --if-not-exists --topic testtopic --bootstrap-server localhost:9092
	docker exec -it kafka /bin/bash /usr/bin/kafka-topics --create --if-not-exists --topic othertopic --bootstrap-server localhost:9092

rabbitmq-start: ## Starts the rabbitmq docker service
	@export PWD=`pwd`
	@cd ${PWD}/docker/rabbitmq; docker-compose up -d
	@cd ${PWD}

rabbitmq-stop: ## Stops the rabbitmq docker service
	@export PWD=`pwd`
	@cd ${PWD}/docker/rabbitmq; docker-compose down
	@cd ${PWD}

kafka-start: ## Starts the kafka docker service
	@export PWD=`pwd`
	@cd ${PWD}/docker/kafka; docker-compose up -d
	@cd ${PWD}

kafka-stop: ## Stops the kafka docker stack
	@export PWD=`pwd`;
	@cd ${PWD}/docker/kafka; docker-compose down;
	@cd ${PWD}

elk-start: ## Starts the ELK stack docker service
	@export PWD=`pwd`;
	@cd ${PWD}/docker/elk; docker-compose up -d;
	@cd ${PWD}

elk-stop: ## Stops the ELK stack docker service
	@export PWD=`pwd`;
	@cd ${PWD}/docker/elk; docker-compose down;
	@cd ${PWD}

start-services: rabbitmq-start kafka-start elk-start ## Starts all dockerized services

stop-services: elk-stop kafka-stop rabbitmq-stop ## Stops all dockerized services

test: ## Runs the tests
	mvn test

container: clean build ## Builds the docker container for this project
	docker build -t events-poc .

clean: ## Cleans the project
	mvn clean

help: ## Prints this help
	@echo 'Usage: make <target>'
	@echo ''
	@echo 'Valid targets:'
	@awk -F ':|##' '/^[^\t].+?:.*?##/ {\
	printf "\033[36m%-30s\033[0m %s\n", $$1, $$NF \
	}' $(MAKEFILE_LIST)