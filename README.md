# Karamel

![Build](https://img.shields.io/badge/Build_with-diligence-orange.svg?style=for-the-badge)
![Java](https://img.shields.io/badge/-Java-orange.svg?style=for-the-badge&logo=Java)
![Quarkus](https://img.shields.io/badge/-Quarkus-orange.svg?style=for-the-badge)
![Camel](https://img.shields.io/badge/-Camel-orange.svg?style=for-the-badge)
![Vue](https://img.shields.io/badge/-Vue.JS-orange.svg?style=for-the-badge&logo=vue.js)
![License](https://img.shields.io/badge/License-Apache-green.svg?style=for-the-badge&logo=apache)

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
Build jdk version
```
mvn package
docker build -t entropy1/karamel .
```
Build native application
```
docker build -f Dockerfile.native -t entropy1/karamel-native .
```
#### Run in Docker Compose with Kafka
Prequisites: Java 11
```
cd compose
docker-compose up -d
docker-compose exec kafka kafka-topics --create --bootstrap-server localhost:9092 -replication-factor 1 --partitions 1 --topic users
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
