# Banking GraphQL API Usage Guide

This guide provides comprehensive examples of how to use the Banking GraphQL API for various operations.

## Table of Contents
1. [Getting Started](#getting-started)
2. [Customer Operations](#customer-operations)
3. [Account Operations](#account-operations)
4. [Transaction Operations](#transaction-operations)
5. [Complex Banking Scenarios](#complex-banking-scenarios)
6. [Error Handling](#error-handling)

## Getting Started

### Access Points
- **GraphQL Endpoint:** http://localhost:8080/graphql
- **GraphiQL UI:** http://localhost:8080/graphiql (Interactive interface)
- **H2 Database Console:** http://localhost:8080/h2-console

### Sample Data
The application starts with pre-loaded sample data:
- 3 Customers (John Doe, Jane Smith, Robert Johnson)
- 4 Accounts with various types
- Initial transactions for each account

You can query this data to test the API immediately.

## Customer Operations

### 1. View All Customers
```graphql
query {
  getAllCustomers {
    id
    firstName
    lastName
    email
    phone
    address
    dateOfBirth
    createdAt
    updatedAt
    accounts {
      accountNumber
      accountType
      balance
      status
    }
  }
}
```

### 2. Get Specific Customer by ID
```graphql
query {
  getCustomerById(id: 1) {
    id
    firstName
    lastName
    email
    phone
    accounts {
      id
      accountNumber
      accountType
      balance
      transactions {
        transactionType
        amount
        createdAt
      }
    }
  }
}
```

### 3. Find Customer by Email
```graphql
query {
  getCustomerByEmail(email: "john.doe@example.com") {
    id
    firstName
    lastName
    email
    accounts {
      accountNumber
      balance
    }
  }
}
```

### 4. Create New Customer
```graphql
mutation {
  createCustomer(input: {
    firstName: "Michael"
    lastName: "Brown"
    email: "michael.brown@example.com"
    phone: "+1-555-0200"
    address: "789 Market St, San Francisco, CA 94103"
    dateOfBirth: "1985-06-15"
  }) {
    id
    firstName
    lastName
    email
    createdAt
  }
}
```

### 5. Update Customer Information
```graphql
mutation {
  updateCustomer(id: 1, input: {
    phone: "+1-555-NEW1"
    address: "999 Updated Street, New York, NY 10001"
  }) {
    id
    firstName
    lastName
    phone
    address
    updatedAt
  }
}
```

### 6. Delete Customer
```graphql
mutation {
  deleteCustomer(id: 10)
}
```
**Note:** This will fail if the customer has active accounts with balances.

## Account Operations

### 1. View All Accounts
```graphql
query {
  getAllAccounts {
    id
    accountNumber
    accountType
    balance
    currency
    status
    createdAt
    customer {
      firstName
      lastName
      email
    }
  }
}
```

### 2. Get Account by ID with Transactions
```graphql
query {
  getAccountById(id: 1) {
    id
    accountNumber
    accountType
    balance
    status
    customer {
      firstName
      lastName
    }
    transactions {
      id
      transactionType
      amount
      description
      balanceAfter
      referenceNumber
      createdAt
    }
  }
}
```

### 3. Get All Accounts for a Customer
```graphql
query {
  getAccountsByCustomerId(customerId: 1) {
    id
    accountNumber
    accountType
    balance
    status
    createdAt
  }
}
```

### 4. Check Account Balance
```graphql
query {
  getAccountBalance(accountNumber: "ACC1729078080538716")
}
```
**Note:** Replace with actual account number from your database.

### 5. Create Savings Account
```graphql
mutation {
  createAccount(input: {
    customerId: 1
    accountType: SAVINGS
    initialDeposit: 10000.0
    currency: "USD"
  }) {
    id
    accountNumber
    accountType
    balance
    status
    createdAt
  }
}
```

### 6. Create Checking Account
```graphql
mutation {
  createAccount(input: {
    customerId: 2
    accountType: CHECKING
    initialDeposit: 2500.0
  }) {
    id
    accountNumber
    accountType
    balance
  }
}
```

### 7. Update Account Status
```graphql
mutation {
  updateAccount(id: 1, input: {
    status: SUSPENDED
  }) {
    id
    accountNumber
    status
    updatedAt
  }
}
```

### 8. Close Account
```graphql
mutation {
  closeAccount(id: 5)
}
```
**Note:** Account must have zero balance to be closed.

## Transaction Operations

### 1. Deposit Money
```graphql
mutation {
  deposit(input: {
    accountNumber: "ACC1729078080538716"
    amount: 2500.0
    description: "Monthly salary deposit"
  }) {
    id
    transactionType
    amount
    description
    status
    balanceAfter
    referenceNumber
    createdAt
  }
}
```

### 2. Withdraw Money
```graphql
mutation {
  withdraw(input: {
    accountNumber: "ACC1729078080538716"
    amount: 800.0
    description: "ATM withdrawal"
  }) {
    id
    transactionType
    amount
    description
    status
    balanceAfter
    referenceNumber
    createdAt
  }
}
```

### 3. Transfer Money Between Accounts
```graphql
mutation {
  transfer(input: {
    sourceAccountNumber: "ACC1729078080538716"
    destinationAccountNumber: "ACC1729078080651923"
    amount: 1500.0
    description: "Payment for consulting services"
  }) {
    success
    message
    sourceTransaction {
      id
      transactionType
      amount
      balanceAfter
      referenceNumber
      relatedTransactionId
    }
    destinationTransaction {
      id
      transactionType
      amount
      balanceAfter
      referenceNumber
      relatedTransactionId
    }
  }
}
```

### 4. View All Transactions
```graphql
query {
  getAllTransactions {
    id
    transactionType
    amount
    description
    status
    balanceAfter
    referenceNumber
    createdAt
    account {
      accountNumber
      customer {
        firstName
        lastName
      }
    }
  }
}
```

### 5. Get Transaction by ID
```graphql
query {
  getTransactionById(id: 1) {
    id
    transactionType
    amount
    description
    status
    balanceAfter
    referenceNumber
    createdAt
    relatedTransactionId
    account {
      accountNumber
      customer {
        firstName
        lastName
      }
    }
  }
}
```

### 6. Get Transactions for an Account
```graphql
query {
  getTransactionsByAccountId(accountId: 1) {
    id
    transactionType
    amount
    description
    balanceAfter
    referenceNumber
    createdAt
  }
}
```

### 7. Get Transactions by Date Range
```graphql
query {
  getTransactionsByDateRange(
    accountId: 1
    startDate: "2024-01-01T00:00:00"
    endDate: "2025-12-31T23:59:59"
  ) {
    id
    transactionType
    amount
    description
    balanceAfter
    createdAt
  }
}
```

## Complex Banking Scenarios

### 1. Open Savings Account with Initial Deposit
This is a compound operation that creates an account and makes an initial deposit in one transaction.

```graphql
mutation {
  openSavingsAccountWithDeposit(
    customerId: 1
    initialDeposit: 25000.0
  ) {
    id
    accountNumber
    accountType
    balance
    status
    createdAt
    customer {
      firstName
      lastName
    }
  }
}
```

### 2. Bulk Transfer (Multiple Transfers at Once)
Process multiple transfers in a single operation. Each transfer is handled independently.

```graphql
mutation {
  bulkTransfer(transfers: [
    {
      sourceAccountNumber: "ACC1729078080538716"
      destinationAccountNumber: "ACC1729078080651923"
      amount: 500.0
      description: "Payment to Vendor A"
    },
    {
      sourceAccountNumber: "ACC1729078080538716"
      destinationAccountNumber: "ACC1729078080760198"
      amount: 750.0
      description: "Payment to Vendor B"
    },
    {
      sourceAccountNumber: "ACC1729078080538716"
      destinationAccountNumber: "ACC1729078080849401"
      amount: 1000.0
      description: "Payment to Vendor C"
    }
  ]) {
    success
    message
    sourceTransaction {
      referenceNumber
      amount
      balanceAfter
      createdAt
    }
    destinationTransaction {
      referenceNumber
      amount
      balanceAfter
    }
  }
}
```

### 3. Generate Account Statement
Get a comprehensive statement with all transactions and summary.

```graphql
query {
  getAccountStatement(
    accountNumber: "ACC1729078080538716"
    startDate: "2024-01-01T00:00:00"
    endDate: "2025-12-31T23:59:59"
  ) {
    accountNumber
    startDate
    endDate
    openingBalance
    closingBalance
    totalDeposits
    totalWithdrawals
    transactionCount
    transactions {
      id
      transactionType
      amount
      description
      balanceAfter
      referenceNumber
      status
      createdAt
    }
  }
}
```

### 4. Complex Nested Query
Retrieve complete customer information with all nested data.

```graphql
query {
  getAllCustomers {
    id
    firstName
    lastName
    email
    phone
    address
    dateOfBirth
    accounts {
      accountNumber
      accountType
      balance
      status
      transactions {
        transactionType
        amount
        description
        balanceAfter
        referenceNumber
        createdAt
      }
    }
  }
}
```

## Error Handling

The API provides clear error messages for various scenarios:

### 1. Insufficient Funds
```graphql
mutation {
  withdraw(input: {
    accountNumber: "ACC1729078080538716"
    amount: 999999.0
    description: "Large withdrawal"
  }) {
    id
  }
}
```
**Error:** "Insufficient funds. Current balance: [amount]"

### 2. Account Not Found
```graphql
query {
  getAccountById(id: 99999) {
    id
  }
}
```
**Error:** "Account not found with id: 99999"

### 3. Duplicate Email
```graphql
mutation {
  createCustomer(input: {
    firstName: "Test"
    lastName: "User"
    email: "john.doe@example.com"
    phone: "+1-555-0000"
    dateOfBirth: "1990-01-01"
  }) {
    id
  }
}
```
**Error:** "Customer already exists with email: john.doe@example.com"

### 4. Invalid Account Status
```graphql
mutation {
  deposit(input: {
    accountNumber: "CLOSED_ACCOUNT_NUMBER"
    amount: 100.0
  }) {
    id
  }
}
```
**Error:** "Account is not active. Current status: CLOSED"

### 5. Negative Amount
```graphql
mutation {
  deposit(input: {
    accountNumber: "ACC1729078080538716"
    amount: -100.0
  }) {
    id
  }
}
```
**Error:** "Deposit amount must be positive"

## Testing with curl

You can also test the API using curl commands:

```bash
# Query example
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ getAllCustomers { id firstName lastName email } }"
  }'

# Mutation example
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { deposit(input: { accountNumber: \"ACC1729078080538716\", amount: 1000.0, description: \"Test deposit\" }) { id amount balanceAfter } }"
  }'
```

## Best Practices

1. **Always check balances** before attempting withdrawals or transfers
2. **Use descriptive descriptions** for all transactions for audit trails
3. **Handle errors gracefully** in your client application
4. **Use account statements** to verify transaction history
5. **Test with small amounts** before executing large transfers
6. **Keep account numbers secure** - treat them as sensitive data
7. **Use date ranges wisely** when querying transaction history

## Transaction Flow Examples

### Complete Banking Workflow

1. **Create a customer**
2. **Open a savings account with initial deposit**
3. **Make additional deposits**
4. **Transfer money to another account**
5. **Generate account statement**
6. **Update customer information if needed**

This workflow demonstrates all major features of the banking system.

## Rate Limiting and Performance

- The API currently has no rate limiting (add for production)
- Bulk operations are atomic - partial failures don't affect other transfers
- Use pagination for large result sets (to be implemented)
- GraphQL allows you to request only the fields you need, improving performance

## Support

For additional help:
- Check the [DOCUMENTATION.md](DOCUMENTATION.md) file
- Review GraphQL schema in GraphiQL interface
- Check application logs for detailed error information
- Inspect H2 database console for raw data

---

**Remember:** This is a demonstration application. Always implement proper security, authentication, and authorization in production environments.
