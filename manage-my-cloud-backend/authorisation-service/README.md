# Manage My Cloud Backend README

## Requirements

- Minimum Java version: v17

## Pre-requisites to running locally:

- Running PostgreSQL db instance in Google Cloud SQL
- Config for DB can be found in DataSourceConfig.java


## Available Scripts

In the project directory, you can run:

### `mvn clean install`
Builds the project, runs all tests and installs all the pom.xml dependencies required for the project to run.

### `mvn spring-boot:run`
Runs the app in the development mode on [http://localhost:8080](http://localhost:8080)

### TODO:

- [ ] Provide configuration options to allow for DB instance overriding for local dev purposed