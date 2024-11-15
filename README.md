# Currency Exchange Application

## Table of Contents

- [Introduction](#introduction)
- [Technologies](#technologies)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Running the Application](#running-the-application)
- [Testing](#testing)

## Introduction

Currency Exchange Application is a service for managing accounts and performing currency exchanges (PLN <-> USD). 
The application uses an external API from the National Bank of Poland (NBP) to fetch current exchange rates.

## Technologies

- Java 21
- Spring Boot
- Spring Data JPA
- Spring MVC
- Lombok
- Redis
- H2 Database
- Swagger

## Configuration
The configuration properties for the datasource, Redis, cache, and the external NBP API URL are located in the `application.properties` file.

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379
cache.expiration.account=1 #cahe for get account set for 1min
cache.expiration.exchange=12 #cache for get exchange rates from nbp set for 12h
nbp.api-url=https://api.nbp.pl/api/exchangerates/rates
```

## Usage

### SWAGGER
All api endpoints are available for inspect while application is running.

[Swagger UI](http://localhost:8080/swagger-ui.html) <i>/swagger-ui.html</i></br>
[Apidoc](http://localhost:8080/v3/api-docs/) <i>/v3/api-docs/</i>


## API Endpoints

### AccountController

- **POST /api/account/create**: Creates a new account
    - **Request Body**: `CreateAccountDTO`
    - **Response**: `AccountResponse`
    - **Example Request**:
      ```json
      {
        "firstName": "Imię",
        "lastName": "Nazwisko",
        "initialPlnBalance": 4563
      }
      ```

- **GET /api/account/{id}**: Fetches account details by ID
    - **Response**: `Account`
    - **Example Request**:
      ```
      GET /api/account/1
      ```

- **DELETE /api/account/{id}**: Deletes an account by ID
    - **Example Request**:
      ```
      DELETE /api/account/1
      ```

### CurrencyExchangeController

- **POST /api/currency/exchange**: Performs a currency exchange
    - **Request Body**: `CurrencyExchangeDTO`
    - **Response**: `CurrencyExchangeResponse`
    - **Example Request**:
      ```json
      {
        "id": 1,
        "fromCurrency": "PLN",
        "amount": 100
      }
      ```
- **POST /api/currency/add**: Adds currency to account
    - **Request Body**: `CurrencyExchangeDTO`
    - **Response**: `CurrencyExchangeResponse`
    - **Example Request**:
      ```json
      {
        "id": 1,
        "fromCurrency": "PLN",
        "amount": 100
      }
      ```
- **POST /api/currency/subtract**: Subtract currency from account
    - **Request Body**: `CurrencyExchangeDTO`
    - **Response**: `CurrencyExchangeResponse`
    - **Example Request**:
      ```json
      {
        "id": 1,
        "fromCurrency": "PLN",
        "amount": 100
      }
      ```

- **GET /api/currency/rates**: Get currency rates
    - **Response**: `Rates`
    - **Example Request**:
      ```
      GET /api/currency/rates
      ```

## Running the Application

To run the application locally, follow these steps:

1. Compile the project:
    ```sh
    mvn clean install
    ```
2. Run the application:
    ```sh
    mvn spring-boot:run
    ```

## Testing

To run unit and integration tests, use the following command:

```sh
mvn test
```
