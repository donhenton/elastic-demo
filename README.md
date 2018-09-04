# elastic-demo
spring-boot and elasticsearch

REST API on top of elasic search

Data is from https://github.com/donhenton/github_load


# Running the Application

## Start Docker Elk
* clone this repository: https://github.com/donhenton/docker-elk (ES frozen at 6.3.0)
* docker-compose up -d

## Run this Application

* mvn clean spring-boot:run -DskipTests
* swagger documentation at http://localhost:9000/swagger-ui.html

## Front End

* https://github.com/donhenton/github-angular
* angular 6 application front-end

