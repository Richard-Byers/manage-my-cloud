# Manage My Cloud Backend README

## Requirements

- Minimum Java version: v17

## Pre-requisites to running locally:

- Postgres docker container must be running on port 5432 named ```postgres```.

To run the postgres docker container you need to change directory to ```manage-my-cloud-backend/authorisation-service```
of the project and run the following command:

### ```docker-compose up -d```

The above command will spin up a postgres container running on port 5432 with the name postgres, before running the
command make sure to fill out any relevant environment variables in the environment section of the docker-compose file.

## Available Scripts

Before attempting to start the application, ensure that you run the following command:

### `mvn clean install`

The clean install command builds the project, runs all tests and installs all the pom.xml dependencies required for the
project to compile.

---
To run the application in the development mode, run the following command in a command-line or alternatively click the
run button bellow:

### `mvn spring-boot:run -Dspring-boot.run.profiles=dev`

The command above runs the SpringBoot application in development mode
on [http://localhost:8080](http://localhost:8080).

---

