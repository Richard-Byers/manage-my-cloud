# Manage My Cloud Backend README

## Requirements

- Minimum Java version: v17

## Pre-requisites to running using docker:

- Postgres docker container must be running on port 5432 named ```postgres```.
- If running the main service in docker: Environment variables must be set in the docker-compose file in the root
  directory of the project.
- If running the authorisation-service locally without docker: Environment variables must be set in your run configuration for
  the service or set in your system environment variables:
  - ONEDRIVE_CLIENT_ID=
  - ONEDRIVE_CLIENT_SECRET=
  - ONEDRIVE_REDIRECT_URI=
  - MMC_EMAIL_PASSWORD=
  - GOOGLE_CREDENTIALS_JSON=
  - "SPRING_PROFILES_ACTIVE=" <- This should be "SPRING_PROFILES_ACTIVE=dev" if running authorisation-service outside of docker, if running in docker it should be "SPRING_PROFILES_ACTIVE=docker"
  - ENCRYPTION_SECRET_KEY= {This will be 1234567891234567 by default if not set}
  - JWT_SECRET_KEY=
  - WEB_CONFIG_ENVIRONMENT= {This will be development by default if not set}

To run both the authorisation-service and the postgres docker containers you need to be in the root directory
of ```manage-my-cloud-backend```

- The environment variables that need to be filled out in the docker-compose file in this directory are:
  - ONEDRIVE_CLIENT_ID=
  - ONEDRIVE_CLIENT_SECRET=
  - ONEDRIVE_REDIRECT_URI=
  - MMC_EMAIL_PASSWORD=
  - GOOGLE_CREDENTIALS_JSON=
  - "SPRING_PROFILES_ACTIVE=docker"
  - ENCRYPTION_SECRET_KEY= {This will be 1234567891234567 by default if not set}
  - JWT_SECRET_KEY=
  - WEB_CONFIG_ENVIRONMENT= {This will be development by default if not set}

then build the docker container using ```docker build -t manage-my-cloud .``` and then run the following command:

### ```docker-compose up -d```

The above command will spin up a postgres container running on port 5432 with the name postgres and the backend authorisation-service.
Before running the
command make sure to fill out any relevant environment variables in the environment section of the docker-compose file.

## Available Scripts

Before attempting to start the application outside of docker, ensure that you run the following command in the ```manage-my-cloud-backend``` root directory:

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

