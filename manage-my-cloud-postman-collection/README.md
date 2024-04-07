# ManageMyCloud API Testing with Postman and Newman

This guide will walk you through the process of setting up and running API tests for the ManageMyCloud service using Postman and Newman.

## Prerequisites

- [Postman](https://www.postman.com/downloads/)
- [Node.js](https://nodejs.org/en/download/)
- [Newman](https://learning.postman.com/docs/running-collections/using-newman-cli/command-line-integration-with-newman/)

## Setup

1. Install Newman globally on your system by running the following command in your terminal:

```bash
npm install -g newman
```

2. Clone the repository and navigate to the directory containing the Postman collection (`ManageMyCoudApiTests.postman_collection.json`) and the environment file (`RegressionSuiteEnv.json`).

## Creating an Account

Before running the tests, you need to create an account on the ManageMyCloud service. Follow the steps below:

1. Register an Account,
2. Link a cloud account,
3. Replace the environment variables in the `RegressionSuiteEnv.json` file with your account details.

```json
{
    "email": "your-email@example.com",
    "password": "your-password",

    "image_path":  "path/to/image.jpg",
    "driveEmail": "email for the cloud account"
  
}
``` 

## Running Tests with Postman

1. Open Postman and import the `ManageMyCoudApiTests.postman_collection.json` collection and the `RegressionSuiteEnv.json` environment file.

2. Select the imported environment from the environment dropdown in the top right corner.

3. Navigate to the "Register User" request and replace `test@example.com` with your email and `password` with your password in the request body.

4. Send the request, and you should receive a 200 status code response.

5. Repeat the process for the other requests in the collection.

## Running Tests with Newman

1. Open your terminal and navigate to the directory containing the Postman collection and the environment file.

2. Run the following command to execute the tests:

```bash
newman run ManageMyCoudApiTests.postman_collection.json -e RegressionSuiteEnv.json
```

3. Newman will run all the tests in the collection and display the results in the terminal.

## Note

Make sure to replace the placeholder values in the requests with your actual data before running the tests. In the Environment file.