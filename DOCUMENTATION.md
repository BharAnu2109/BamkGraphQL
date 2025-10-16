# Banking GraphQL Microservices Documentation

## Overview
This is a comprehensive Spring Boot microservices banking application with GraphQL API. The application provides a complete banking system with customer management, account operations, and transaction processing including complex scenarios like transfers and bulk operations.

## Technology Stack
- **Spring Boot 3.1.5** - Application framework
- **Spring Data JPA** - Data persistence layer
- **Spring GraphQL** - GraphQL implementation
- **H2 Database** - In-memory database for dynamic data storage
- **Lombok** - Reduce boilerplate code
- **Java 17** - Programming language

## Features

### 1. Customer Management
- Create, read, update, and delete customers
- Unique email validation
- Customer profile management
- Link multiple accounts to customers

### 2. Account Management
- Multiple account types: SAVINGS, CHECKING, FIXED_DEPOSIT, CURRENT
- Account status management: ACTIVE, INACTIVE, SUSPENDED, CLOSED
- Account balance tracking
- Currency support (default: USD)
- Automatic account number generation

### 3. Transaction Operations
- **Deposits** - Add funds to accounts
- **Withdrawals** - Remove funds from accounts
- **Transfers** - Move funds between accounts
- **Bulk Transfers** - Process multiple transfers in one operation
- Transaction history tracking
- Account statements with date range filtering

### 4. Complex Banking Scenarios
- **Insufficient funds validation**
- **Account status validation** before transactions
- **Atomic transfers** with related transaction linking
- **Account closure** with balance verification
- **Bulk operations** with error handling per transaction
- **Account statements** with opening/closing balances and totals

## Project Structure

```
src/main/java/com/banking/graphql/
├── model/              # Entity models
│   ├── Customer.java
│   ├── Account.java
│   ├── Transaction.java
│   └── Enums (AccountType, AccountStatus, TransactionType, TransactionStatus)
├── repository/         # JPA repositories
│   ├── CustomerRepository.java
│   ├── AccountRepository.java
│   └── TransactionRepository.java
├── service/           # Business logic
│   ├── CustomerService.java
│   ├── AccountService.java
│   └── TransactionService.java
├── resolver/          # GraphQL resolvers
│   ├── QueryResolver.java
│   ├── MutationResolver.java
│   └── Field resolvers (Customer, Account, Transaction)
├── dto/              # Data transfer objects
│   ├── AccountStatement.java
│   └── TransferResult.java
├── exception/        # Custom exceptions and handlers
│   ├── ResourceNotFoundException.java
│   ├── InsufficientFundsException.java
│   ├── InvalidAccountException.java
│   ├── DuplicateResourceException.java
│   └── GraphQLExceptionHandler.java
├── config/           # Configuration classes
│   └── DataInitializer.java
└── BankingGraphQLApplication.java  # Main application class
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Build the project:**
```bash
mvn clean install
```

2. **Run the application:**
```bash
mvn spring-boot:run
```

3. **Access the endpoints:**
- GraphQL API: http://localhost:8080/graphql
- GraphiQL UI: http://localhost:8080/graphiql
- H2 Console: http://localhost:8080/h2-console

### H2 Database Configuration
- **URL:** jdbc:h2:mem:bankingdb
- **Username:** sa
- **Password:** (empty)

## GraphQL API Examples

### Customer Operations

#### Create Customer
```graphql
mutation {
  createCustomer(input: {
    firstName: "Alice"
    lastName: "Williams"
    email: "alice.williams@example.com"
    phone: "+1-555-0104"
    address: "321 Elm St, Boston, MA"
    dateOfBirth: "1988-12-05"
  }) {
    id
    firstName
    lastName
    email
    createdAt
  }
}
```

#### Get All Customers
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

#### Get Customer by ID
```graphql
query {
  getCustomerById(id: 1) {
    id
    firstName
    lastName
    email
    phone
    accounts {
      accountNumber
      balance
      accountType
    }
  }
}
```

### Account Operations

#### Create Account
```graphql
mutation {
  createAccount(input: {
    customerId: 1
    accountType: SAVINGS
    initialDeposit: 1000.0
    currency: "USD"
  }) {
    id
    accountNumber
    accountType
    balance
    status
  }
}
```

#### Get Account Balance
```graphql
query {
  getAccountBalance(accountNumber: "ACC1234567890123")
}
```

#### Get Accounts by Customer
```graphql
query {
  getAccountsByCustomerId(customerId: 1) {
    id
    accountNumber
    accountType
    balance
    status
  }
}
```

### Transaction Operations

#### Deposit Money
```graphql
mutation {
  deposit(input: {
    accountNumber: "ACC1234567890123"
    amount: 500.0
    description: "Salary deposit"
  }) {
    id
    transactionType
    amount
    balanceAfter
    referenceNumber
    createdAt
  }
}
```

#### Withdraw Money
```graphql
mutation {
  withdraw(input: {
    accountNumber: "ACC1234567890123"
    amount: 200.0
    description: "ATM withdrawal"
  }) {
    id
    transactionType
    amount
    balanceAfter
    referenceNumber
  }
}
```

#### Transfer Money
```graphql
mutation {
  transfer(input: {
    sourceAccountNumber: "ACC1234567890123"
    destinationAccountNumber: "ACC9876543210987"
    amount: 300.0
    description: "Payment to vendor"
  }) {
    success
    message
    sourceTransaction {
      id
      amount
      balanceAfter
    }
    destinationTransaction {
      id
      amount
      balanceAfter
    }
  }
}
```

#### Bulk Transfer
```graphql
mutation {
  bulkTransfer(transfers: [
    {
      sourceAccountNumber: "ACC1234567890123"
      destinationAccountNumber: "ACC9876543210987"
      amount: 100.0
      description: "Payment 1"
    },
    {
      sourceAccountNumber: "ACC1234567890123"
      destinationAccountNumber: "ACC5555555555555"
      amount: 150.0
      description: "Payment 2"
    }
  ]) {
    success
    message
    sourceTransaction {
      referenceNumber
      amount
    }
  }
}
```

### Account Statement
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

### Complex Banking Operations

#### Open Savings Account with Initial Deposit
```graphql
mutation {
  openSavingsAccountWithDeposit(
    customerId: 1
    initialDeposit: 5000.0
  ) {
    id
    accountNumber
    accountType
    balance
    status
  }
}
```

## Error Handling

The application includes comprehensive error handling:

- **ResourceNotFoundException** - When requested resource doesn't exist
- **InsufficientFundsException** - When account has insufficient balance
- **InvalidAccountException** - When account status is invalid for operation
- **DuplicateResourceException** - When trying to create duplicate resources

## Sample Data

The application initializes with sample data:

**Customers:**
- John Doe (john.doe@example.com) - 2 accounts
- Jane Smith (jane.smith@example.com) - 1 account
- Robert Johnson (robert.j@example.com) - 1 account

**Accounts:**
- Multiple savings, checking, and current accounts with initial deposits

## Database Schema

### Customers Table
- id, first_name, last_name, email, phone, address, date_of_birth, created_at, updated_at

### Accounts Table
- id, account_number, account_type, balance, currency, status, customer_id, created_at, updated_at

### Transactions Table
- id, transaction_type, amount, currency, description, status, account_id, reference_number, balance_after, created_at, related_transaction_id

## Advanced Features

1. **Automatic Timestamps** - Created and updated timestamps managed automatically
2. **Unique Reference Numbers** - Auto-generated for all transactions
3. **Account Number Generation** - Automatic generation on account creation
4. **Transaction Linking** - Related transactions (transfers) are linked via IDs
5. **Balance Tracking** - Each transaction stores the balance after completion
6. **Data Validation** - Comprehensive validation using Jakarta Validation
7. **Transaction Management** - ACID compliance with Spring's @Transactional

## Testing

You can test the API using:
1. **GraphiQL UI** - Interactive GraphQL IDE at http://localhost:8080/graphiql
2. **Postman** - Send POST requests to http://localhost:8080/graphql
3. **curl** - Command-line testing

Example curl request:
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ getAllCustomers { id firstName lastName email } }"}'
```

## Security Considerations

For production deployment, consider adding:
- Authentication and authorization (Spring Security)
- Rate limiting
- Input sanitization
- Database encryption
- Audit logging
- HTTPS/TLS

## Performance Optimization

- Uses Lazy Loading for relationships
- Efficient queries with JPA
- Connection pooling
- In-memory H2 database for fast operations

## Future Enhancements

Potential features to add:
- Interest calculation for savings accounts
- Loan management
- Credit card operations
- Multi-currency support with exchange rates
- Scheduled payments
- Notification system
- Account statements in PDF format
- Advanced analytics and reporting

## License

This is a demonstration project for educational purposes.

## Support

For issues or questions, please refer to the project documentation or create an issue in the repository.
