#Event processing POC

##What this project is

This small project is meant to be an implementation of a minimalistic 
event processing consumer and publisher to demo a few concepts, such as 
hexagonal architectures, separation of concerns, containerization, and probably a few others

This project contains 2 main packages
- "events": contains all the interfaces and adapters
- "application": meant to be the userspace application (aka YOUR app)

There are no cross dependencies in the project, other than the application depending on interfaces.
There are no technological dependencies in the application either

##What this project isn't

- perfect
- fully parameterized
- production grade
- secure

##Known issues / improvements

- Publishing is direct and not buffered
- Some values are hardcoded
- Some configuration is hardcoded
- package internal structure (directories) could be better
- app docker container should be parameterized with vars for configuration
- service docker containers should be using docker volumes instead of mounted directories 
- kafka topic initialization should be based on configuration and not be hardcoded
- rabbitmq queue/exchange/binding initialization should be based on configuration and not be hardcoded
- tests should be better structured into directories and not just all in one directory
- actual implementation for consumers and publishes are a 100% copy-paste from example code and could use massive improvements
- docker compose files could use an overhaul

###This project
- might not be the most optimally set up based on java project setup "best practices"
- is using c# style interface naming and zero annotations in the main application *bite me*
- is missing some formatting
- might not be implemented *the java way*

##Pre-requisites

First and foremost you will need GNU make
If you have that you can run ``make check-deps`` for a rudementary dependency check 
This will check for things you need (not for specific versions though just yet)

You will definitely need the following things:
- java 8 (tested with openJDK version 1.8.0_282)
- docker (tested with 12.03.13)
- docker-compose (tested with 1.25.0)
- maven (tested with 3.6.3)

##How to set the project up

This look long and complicated, but don't worry, it is really just a few commands to run and a few values to change (if you want to), the rest is just explanation.

1. Check that you have everything with ``make check-deps``
2. Go into the ``docker`` directory inside the project, you will find 3 other directories (kafka, rabbitmq and elk) with ``.env.example`` files in them. These files are provided for your convenience. Make a copy of each of them in the same directory, named ``.env``. The only thing I'd recommend for you to change in the copied ``.env`` files at this point is the HOME directory for each service. By default this is configured to use the current project directory to store persistent data generated by these services, I'd recommend changing them to somewhere outside the project directory, for example "/srv/docker-data/<servicename>" (Please note, that you cannot use "~" or "$HOME" in the path, these should be absolute paths)
3. Run ``make init``. This will create the common network the docker containers use, and create the persistent data mounts for the docker containers
4. Run ``make start-services``. This is an aggregated make goal to start the rabbitmq server, the kafka server and the entire ELK stack. You can also start them individually by running ``make rabbitmq-start``, ``make kafka-start``, and ``make elk-start`` respectively (In order to shut them down, run ``make stop-services``, or ``make rabiitmq-stop``, make ``kafka-stop`` and ``make elk-stop``)
5. Please check if they are all running, you can do this by individually verify/open the web UI for each service.
   - __RabbitMQ__: visit "http://localhost15762" to see the rabbitmq management ui (the default username is 'admin', and the default password is 'admin123')
   - __Kafka__: visit "http://localhost:9000" to view KafDrop (no user/pass required)
   - __ELK__: visit "http://localhost:5601" to open kibana (you might need to give it a minute, this one is quite slow to start up)
   - You can also verify these are running by checking ``docker ps`` which should list running containers:
        - RabbitMQ should be a single container 
        - Kafka should be 3: Kafka, Zookeeper and KafDrop
        - elk should be another 3: logstash, elasticsearch and kibana
6. Initialize the queues and topics for both queue systems (the containers must be running at this point)
   - Run ``make rabbitmq-setup`` to set up all queues, exchanges and bindings
   - Run ``make kafka-setup`` to set up both topics
    
You should be good to go at this point (The ELK stack might need restarting since it will initially try to consume messages from sources that didn't exist at that time)

##Setting up kibana

There is a bonus step for setting up the index for kibana, this can only be done after at least 1 message got published
Once that's done (you can verify the message in KafDrop or rabbitMQ management), you should:
1. Visit "http://localhost:5601"
2. Click the big box titled "Kibana" (on the right side)
3. Click "Add you data"
4. Click "Create Index Pattern"
5. You should see a source in the list titles "message-<date>"
6. Type in "messages-*" into the "index pattern name" box and click "Next"
7. Select either "@timestamp" or "occurred_at" from the dropdown menu and click Done 

You should be able to see messages coming through now if you select "Discover" from the hamburger menu on the top left side (Under Analytics) 

##How to build the project

This one is easy, just run ``make`` (or ``make build``)

##How to run the tests

Also easy, just run ``make test``

##Hot to run the project

There are 2 ways of running the code, by hand/from your IDE, or as a docker container

###Running the code by hand

After building the project, you should be able to run the main application by running ``java -jar target/gs-maven-0.1.0.jar``
This will start the currently configured consumer

###Running the code from your IDE

Set the project up in your favorite IDE, and click run(?)
You can run 2 scripts this way, "MyApplication" will run the consumer (whichever one is configured in the container), or run "QuickPublish" which will just simply publish a ``transaction_cleared`` event. 

###Running the code as a docker container

In order to run the project in a docker container:
1. Build the container by running ``make container``
2. Run ``docker run --network=poc-events events-poc`` (or simply run ``docker-compose up`` from the project root directory)

##How does the processing work / what should I see?

The application has 2 messages ``transaction_cleared`` and ``calculate_charges``
In the current setup, there is a single handler listening to ``transaction_cleared`` and publishing ``calculate_charges`` as a consequence, therefore in order for you to see anything happening you should kick the process off by publishing a ``transaction_cleared`` event.
You can publish this message either by hand in the web UI, or use a small script was provided for your convenience called "QuickPublish" in the main application (just run QuickPublish.java from you IDE)

Once the ``transaction_cleared`` event is published, (and the consumer is running, or started up after) you should see it processing the event, handling it, and publishing the ``calculate_charges`` command as a response to it.
What exactly happens depends on which consumer is configured, it's a difference of how the 2 are set up.

__Kafka__:
 
---publish transaction_cleared event to---> (T)testtopic   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;---consumed by---> MyApplication   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;---publish calculate_charges to---> (T)othertopic   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;---> END 

(T): Topic

Both "testtopic" and "othertopic" is also consumed by elk

__RabbitMQ__:

---publish transaction_cleared event to ---> (X)events  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;--- gets routed to ---> (Q)logging AND  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;--- gets routed to ---> (Q)testqueue  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;---consumed by---> MyApplication  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;---publish calculate_charges to---> (X)commands  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;--- gets routed to ---> (Q)logging AND    
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;--- gets routed to ---> (Q)testqueue  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;---consumed by---> MyApplication  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;---> END 

(X): Exchange  
(Q): Queue

the "logging" queue is consumed by elk
