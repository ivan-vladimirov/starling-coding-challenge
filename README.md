# Starling Bank API Technical Task
Implemenation by Ivan Ivanov.
A technical implementation of a round up feature for using Starling API. This project takes transactions from the last 7 days to compute how much the round up should be.
## How to run
Compile the application with:
```
"mvn clean install"
```
You have the option to run using Docker by running the following:
```
docker run -p 8080:8080 starling-bank
docker run -p 8080:8080 -e TOKEN="{Token obtained by the OAuth API}" starling-bank
```
Or run it directly using Maven.
## Requirements
Token must be obtained from Starling OAuth API before running the application. Without a valid token the application will not run properly.

# Reasoning behind solution
In this section I will clarify some of my decisions behind the implementation.

Firstly, having the token as an environment variable is not an optimal solution, however, I felt like it fit the nature of the project (interview coding task). In a production environemnt I would implement an OAuth client that takes the token directly from Starling API's Sandbox. 

Second, the implementation has some assumptions for the way the API works. For example, I have assumed that in a PRIMARY account you can only have GBP feed items because every EUR transaction goes to the ADDITIONAL one. Again in a Production environment way more edge case testing is going to be present. However despite the time limitation I believe I have implemented a fairly decent code coverage tests which exploit the most common edge cases.

I have included a Docker file for deployment but decided not to include any k8s deployment.yml files etc.

# How to use or test
## API Endpoints and usage
All endpoints have example parameters in the {} brackets. :)
- GET: http://localhost:8080/api/accounts
This endpoint returns all of the accounts e.g PRIMARY and ADDITIONAL

- GET: http://localhost:8080/api/transactions?fromDate={2022-06-27T12:34:56.000Z}
This endpoint returns all of the transactions for all of the accounts the user has since a given date. 
The enpoint takes fromDate parameter which needs to be in the same format as the ZonedDateTime in StarlingAPI

- GET: http://localhost:8080/api/transactions/byId?accountUid={8b31444e-92cd-4a1f-893a-15a3c3a8f35a}&fromDate={2022-06-27T12:34:56.000Z}
This endpoint returns all transactions for specific accountUID since a given date.
The endpoint has 2 parameters:
FromDate - ZonedDateTime which must be formatted the way it is in StralingAPI
AccountUID - Obtained either from the API or StarlingAPI

- GET: http://localhost:8080/api/roundUpWeek
This endpoints returns an Amount object with the sum of all of the rounded transactions per Account.

- GET: http://localhost:8080/api/roundUpWeek/byUid?accountUid={8b31444e-92cd-4a1f-893a-15a3c3a8f35a}
This endpoint rounds up all transactions for specific account for the past 7 days. You must specify accountUid in parameters. 
Returns Amount same way as the API

- PUT: http://localhost:8080/api/roundUpWeek?savingsGoalUid={2a786ed1-dae6-4dce-9aa3-e572ab0a00e7}&accountUid={8b31444e-92cd-4a1f-893a-15a3c3a8f35a}
This endpoint is triggering the rounding up of all transactions for specified account and putting them in the savingsGoal specified.
The endpoint has 2 parameters:
SavingsGoalUid - obtained from StarlingAPI
AccountUid - obtained from API or StarlingAPI
## Test
You can run all of the test either using the IDE or by running ```mvn test```
