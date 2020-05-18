# Civil Servant Registry Service


CRUD services to hold employment and personal details of civil servants.

Service uses [spring-data-rest](https://projects.spring.io/spring-data-rest/) to expose resources.

## Getting Started
### Environment Variables
You will need to add the following env variables to run code locally, or to run the test suite.

| VARIABLE | DESCRIPTION | DEFAULT |
|--|--|--|
|CLIENT_ID | Identity Service client ID |NO|
|CLIENT_SECRET|Identity Service client secret|NO|
|CHECK_TOKEN_URL|Identity service check token url|NO|
|DATASOURCE|The Datasource connectiong string|NO|
|GOV_NOTIFY_API_KEY| Gov Notify API Key |NO|
|GOV_NOTIFY_LINEMANAGER_TEMPLATE_ID| Template for Email sent to linemanager|NO|
|OAUTH_SERVICE_URL| Url to OAUTH service|http://localhost:8080|
|ROOT_LOGGING_LEVEL| Logging level|info|
### Build
Build the application using Gradle ```./gradlew build```  

Run the project with Gradle or ```./gradlew bootRun``` import project into IntelliJ and Run Application.  

The application uses HAL and self exploring.
`localhost:9002`  


## Requirements

A Backing storage solution is required. 
