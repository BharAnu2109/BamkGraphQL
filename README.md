# Banking GraphQL Microservices Application

A comprehensive Spring Boot microservices banking application with GraphQL API that handles complex banking scenarios with dynamic data management.

## 🚀 Features

- **Customer Management** - Create, update, delete, and query customers
- **Account Management** - Multiple account types (Savings, Checking, Fixed Deposit, Current)
- **Transaction Processing** - Deposits, withdrawals, and transfers
- **Complex Banking Operations** - Bulk transfers, account statements, balance tracking
- **GraphQL API** - Modern API with GraphiQL interface
- **Dynamic Data Storage** - H2 in-memory database
- **Error Handling** - Comprehensive exception handling
- **Docker Support** - Containerized deployment

## 🏗️ Architecture

### Technology Stack
- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Data JPA**
- **Spring GraphQL**
- **H2 Database**
- **Lombok**
- **Maven**

### Microservices Components
- Customer Service
- Account Service  
- Transaction Service
- GraphQL API Gateway

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker (optional, for containerized deployment)

## 🛠️ Installation & Setup

### Clone the Repository
```bash
git clone https://github.com/BharAnu2109/BamkGraphQL.git
cd BamkGraphQL
```

### Build the Project
```bash
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

### Access the Application
- **GraphQL Endpoint:** http://localhost:8080/graphql
- **GraphiQL UI:** http://localhost:8080/graphiql
- **H2 Console:** http://localhost:8080/h2-console
  - URL: `jdbc:h2:mem:bankingdb`
  - Username: `sa`
  - Password: (leave empty)

## 🐳 Docker Deployment

### Build and Run with Docker Compose
```bash
docker-compose up --build
```

### Build Docker Image
```bash
docker build -t banking-graphql:latest .
```

### Run Docker Container
```bash
docker run -p 8080:8080 banking-graphql:latest
```

## 📝 GraphQL API Examples

### Query: Get All Customers
```graphql
query {
  getAllCustomers {
    id
    firstName
    lastName
    email
    accounts {
      accountNumber
      accountType
      balance
    }
  }
}
```

### Mutation: Create Customer
```graphql
mutation {
  createCustomer(input: {
    firstName: "John"
    lastName: "Doe"
    email: "john.doe@example.com"
    phone: "+1-555-0101"
    address: "123 Main St, New York"
    dateOfBirth: "1990-01-15"
  }) {
    id
    firstName
    lastName
    email
  }
}
```

### Mutation: Deposit Money
```graphql
mutation {
  deposit(input: {
    accountNumber: "ACC1234567890123"
    amount: 1000.0
    description: "Salary deposit"
  }) {
    id
    transactionType
    amount
    balanceAfter
    referenceNumber
  }
}
```

### Mutation: Transfer Money
```graphql
mutation {
  transfer(input: {
    sourceAccountNumber: "ACC1234567890123"
    destinationAccountNumber: "ACC9876543210987"
    amount: 500.0
    description: "Payment"
  }) {
    success
    message
    sourceTransaction {
      referenceNumber
      balanceAfter
    }
    destinationTransaction {
      referenceNumber
      balanceAfter
    }
  }
}
```

### Query: Get Account Statement
```graphql
query {
  getAccountStatement(
    accountNumber: "ACC1234567890123"
    startDate: "2024-01-01T00:00:00"
    endDate: "2024-12-31T23:59:59"
  ) {
    accountNumber
    openingBalance
    closingBalance
    totalDeposits
    totalWithdrawals
    transactionCount
    transactions {
      transactionType
      amount
      description
      createdAt
    }
  }
}
```

## 🎯 Complex Banking Scenarios Implemented

1. **Account Management**
   - Multiple account types support
   - Account status management (Active, Inactive, Suspended, Closed)
   - Balance validation before operations

2. **Transaction Processing**
   - Atomic transfers with rollback support
   - Insufficient funds validation
   - Transaction reference number generation
   - Balance tracking after each transaction

3. **Bulk Operations**
   - Bulk transfer with individual error handling
   - Multiple transactions in single request
   - Partial success handling

4. **Account Statements**
   - Date range filtering
   - Opening and closing balance calculation
   - Transaction categorization
   - Summary statistics

5. **Data Validation**
   - Email uniqueness
   - Account number uniqueness
   - Positive amount validation
   - Account status verification

## 📊 Database Schema

### Customers
- id, first_name, last_name, email, phone, address, date_of_birth, created_at, updated_at

### Accounts
- id, account_number, account_type, balance, currency, status, customer_id, created_at, updated_at

### Transactions
- id, transaction_type, amount, currency, description, status, account_id, reference_number, balance_after, created_at, related_transaction_id

## 🔧 Configuration

Edit `src/main/resources/application.yml` to customize:
- Database settings
- GraphQL endpoint paths
- Server port
- Logging levels

## 🧪 Testing

### Using GraphiQL
1. Navigate to http://localhost:8080/graphiql
2. Use the interactive UI to test queries and mutations
3. Explore the schema documentation

### Using curl
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ getAllCustomers { id firstName lastName } }"}'
```

## 📁 Project Structure

```
src/main/java/com/banking/graphql/
├── config/              # Configuration and initialization
├── dto/                 # Data Transfer Objects
├── exception/           # Custom exceptions and handlers
├── model/              # Entity models
├── repository/         # JPA repositories
├── resolver/           # GraphQL resolvers
├── service/            # Business logic
└── BankingGraphQLApplication.java
```

## 🌟 Sample Data

The application initializes with sample data:
- 3 Customers
- 4 Accounts (various types)
- Initial transactions for each account

## 📖 Documentation

For detailed documentation, see [DOCUMENTATION.md](DOCUMENTATION.md)

## 🔒 Security Notes

This is a demonstration application. For production use, implement:
- Authentication & Authorization (Spring Security)
- Input validation & sanitization
- Rate limiting
- HTTPS/TLS encryption
- Database encryption
- Audit logging

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is for educational and demonstration purposes.

## 👨‍💻 Author

BharAnu2109

## 📞 Support

For issues or questions, please create an issue in the GitHub repository.

---

**Built with ❤️ using Spring Boot and GraphQL**
