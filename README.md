# Karamel

Simple Kafka Browser that supports standalone Kafka and Strimzi operator.

- Kafka cluster status
- Topic list
- Consumer (filter WIP)
- Producer 
- Strimzi Operator status (Kubernetes only)
- Zookepers status (Kubernetes only)
- Kafka resources status (Kubernetes only)

## Running in Minikube

#### Install strimzi 
Prequisites: Minikube, Ansible
```
ansible-playbook minikube/install.yaml 
```
#### Deploy karamel
Prequisites: Java 11
```
ansible-playbook minikube/deploy.yaml 
```

## Running in Docker compose

#### Build Karamel Docker 
Prequisites: Docker Desktop
```
docker build -f Dockerfile.multistage -m 12g -t entropy1/karamel .
```
#### Run in Docker Compose with Kafka
Prequisites: Java 11
```
cd compose
docker-compose up 
```

## Development
#### Running the application in dev mode
```
mvn quarkus:dev
```
#### Packaging and running the application
The application is packageable using 
```
mvn package
java -jar target/karamel-1.0.0-SNAPSHOT-runner.jar
```


### Screenshots
#### Kafka
![Screenshot](docs/img/kafka.png)
#### Strimzi operator
![Screenshot](docs/img/operator.png)
#### Zookeeper
![Screenshot](docs/img/zookeeper.png)
#### Topics
![Screenshot](docs/img/topics.png)
#### Client - consumer
![Screenshot](docs/img/client-consumer.png)
#### Client - producer
![Screenshot](docs/img/client-producer.png)
