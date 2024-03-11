# Manage My Cloud Frontend Selenium Tests README

Developed using Python 3.10

## Pre-requisites to running the tests:

- Python 3.10 must be installed on your machine.
- Install the required packages by running the following command in the terminal:
  - `pip install -r requirements.txt`
- Download the appropriate version of the ChromeDriver if you don't have it from [here](https://sites.google.com/chromium.org/driver/).
- Valid user in the database
  - Email: managemycloudtester@gmail.com
  - Firstname: test
  - Lastname: test
- Valid user doesn't have any linked accounts
- Valid user has one file (<1-week-old) in OneDrive account
- environment variables related to valid user must be set:
  - `TEST_EMAIL`
  - `TEST_EMAIL_PASSWORD` for ease of testing make this password the same as ONEDRIVE_TEST_PASSWORD
  - `ONEDRIVE_TEST_PASSWORD`

## Running the tests:
-cd into the `pages` directory and run the following command:
- `python execute_suite.py` or `python3 execute_suite.py`

