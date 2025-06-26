# Bank and Tax Authority Demo Application

This is a Java Spring Boot application that demonstrates a banking system with tax authority monitoring capabilities. The application uses an in-memory H2 database for data persistence.

## Technology Stack

- Java 17
- Spring Boot 3.5.3
- Spring Data JPA
- H2 In-Memory Database
- Spring AMQP with RabbitMQ for asynchronous messaging

## Project Structure

- `model` package: Contains the domain models (Client, Account, Currency) as JPA entities
- `repository` package: Contains JPA repositories for data persistence
- `service` package: Contains the business logic (BankService, FiscService)
- `controller` package: Contains the REST API controllers (ClientController, FiscController)
- `DemoRunner`: A command-line runner that demonstrates the functionality

## Database

The application uses an H2 in-memory database for data persistence. The database console is available at:

```
http://localhost:8080/h2-console
```

Connection details (default):
- JDBC URL: `jdbc:h2:mem:bankdb`
- Username: `sa`
- Password: (empty)

## Features

### Bank
- Stores client information: CNP, RON account balance, EUR account balance
- Persists data using JPA entities and repositories
- Provides interfaces for clients and tax authority
- Supports account creation, closure, deposits, withdrawals, and balance inquiries
- Notifies tax authority when monitored clients' balances change

### Client
- Can create and close accounts
- Can deposit and withdraw money (minimum balance of 1000 RON/EUR required)
- Can check account balances

### Tax Authority (FISC)
- Can monitor multiple clients
- Caches client information
- Receives notifications asynchronously via RabbitMQ when monitored clients' balances change
- Displays messages about which accounts had balance changes

## How to Run

### Option 1: Running locally

1. Navigate to the project directory
2. Build the project: `mvn clean package`
3. Run the application: `java -jar target/banking-0.0.1-SNAPSHOT.jar`

The application includes a demo that will run automatically and show the functionality.

### Option 2: Running with Docker

The application can be run using Docker and Docker Compose, which will set up both the Spring Boot application and RabbitMQ.

1. Navigate to the project directory
2. Build and start the containers:
   ```
   docker-compose up -d
   ```
3. View logs:
   ```
   docker-compose logs -f bank-fisc-app
   ```
4. Access RabbitMQ Management UI at http://localhost:15672 (username: guest, password: guest)
5. To stop the containers:
   ```
   docker-compose down
   ```

## API Endpoints

### Client API
- `POST /api/clients/{cnp}` - Create accounts for a client
- `DELETE /api/clients/{cnp}` - Close accounts for a client
- `GET /api/clients/{cnp}` - Get account information
- `POST /api/clients/{cnp}/deposit?currency=RON&amount=1000` - Deposit money
- `POST /api/clients/{cnp}/withdraw?currency=EUR&amount=500` - Withdraw money

### FISC API
- `POST /api/fisc/monitor/{cnp}` - Start monitoring a client
- `DELETE /api/fisc/monitor/{cnp}` - Stop monitoring a client

### Client API Commands
1. Create Accounts for a Client   
   ```
   curl -X POST "http://localhost:8080/api/clients/1234567890123" -H "Content-Type: application/json"
   ```
2. Get Client Account Information
   ```
   curl -X GET "http://localhost:8080/api/clients/1234567890123" -H "Accept: application/json"
   ```
3. Deposit Money   
   
   ```
   curl -X POST "http://localhost:8080/api/clients/1234567890123/deposit?currency=RON&amount=2000.00" -H "Content-Type: application/json"
   ```
   ```
   curl -X POST "http://localhost:8080/api/clients/1234567890123/deposit?currency=EUR&amount=1500.00" -H "Content-Type: application/json"
   ```
4. Withdraw Money
   ```
   curl -X POST "http://localhost:8080/api/clients/1234567890123/withdraw?currency=RON&amount=1500.00" -H "Content-Type: application/json"
   ```
   ```
   curl -X POST "http://localhost:8080/api/clients/1234567890123/withdraw?currency=EUR&amount=1000.00" -H "Content-Type: application/json"
   ```
5. Close Client Accounts
   ```
   curl -X DELETE "http://localhost:8080/api/clients/1234567890123" -H "Content-Type: application/json"
   ```
   
### FISC (Tax Authority) API Commands
1. Start Monitoring a Client
   ```
   curl -X POST "http://localhost:8080/api/fisc/monitor/1234567890123" -H "Content-Type: application/json"
   ```
2. Stop Monitoring a Client
   ```
   curl -X DELETE "http://localhost:8080/api/fisc/monitor/1234567890123" -H "Content-Type: application/json"
   ```