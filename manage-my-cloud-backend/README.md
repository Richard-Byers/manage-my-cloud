# Manage My Cloud Backend README

## Requirements

- Minimum Java version: v17

## Pre-requisites to running using docker:

- Postgres docker container must be running on port 5432 named ```postgres```.
- If running the main service in docker: Environment variables must be set in the docker-compose file in the root
  directory of the project.
- If running the main service locally without docker: Environment variables must be set in your run configuration for
  the service or set in your system environment variables.

To run both the service and the postgres docker containers you need to be in the root directory
of ```manage-my-cloud-backend```,
build the docker container using ```docker-build -t manage-my-cloud .``` and then run the following command:

### ```docker-compose up -d```

The above command will spin up a postgres container running on port 5432 with the name postgres and the backend service.
Before running the
command make sure to fill out any relevant environment variables in the environment section of the docker-compose file.

- The environment variables that need to be filled out in the docker-compose file in this directory are:
    - ONEDRIVE_CLIENT_ID
    - ONEDRIVE_CLIENT_SECRET
    - ONEDRIVE_REDIRECT_URI
    - MMC_EMAIL_PASSWORD

## Available Scripts

Before attempting to start the application, ensure that you run the following command:

### `mvn clean install`

The clean install command builds the project, runs all tests and installs all the pom.xml dependencies required for the
project to compile.

---
To run the authorisation-service outside of docker, CD to the authorisation-service directory and run the following
command in the terminal:

- ### `mvn spring-boot:run -Dspring-boot.run.profiles=dev`

IF YOU'RE RUNNING BOTH POSTGRES AND AUTHORISATION-SERVICE IN DOCKER, YOU CAN SKIP THE ABOVE COMMAND.

The commands above run the SpringBoot application in development mode
on [http://localhost:8080](http://localhost:8080).

---

