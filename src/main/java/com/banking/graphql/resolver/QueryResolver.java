package com.banking.graphql.resolver;

import com.banking.graphql.dto.AccountStatement;
import com.banking.graphql.model.Account;
import com.banking.graphql.model.Customer;
import com.banking.graphql.model.Transaction;
import com.banking.graphql.service.AccountService;
import com.banking.graphql.service.CustomerService;
import com.banking.graphql.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class QueryResolver {
    
    private final CustomerService customerService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    
    // Customer Queries
    @QueryMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }
    
    @QueryMapping
    public Customer getCustomerById(@Argument Long id) {
        return customerService.getCustomerById(id);
    }
    
    @QueryMapping
    public Customer getCustomerByEmail(@Argument String email) {
        return customerService.getCustomerByEmail(email);
    }
    
    // Account Queries
    @QueryMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }
    
    @QueryMapping
    public Account getAccountById(@Argument Long id) {
        return accountService.getAccountById(id);
    }
    
    @QueryMapping
    public List<Account> getAccountsByCustomerId(@Argument Long customerId) {
        return accountService.getAccountsByCustomerId(customerId);
    }
    
    @QueryMapping
    public Double getAccountBalance(@Argument String accountNumber) {
        return accountService.getAccountBalance(accountNumber);
    }
    
    // Transaction Queries
    @QueryMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }
    
    @QueryMapping
    public Transaction getTransactionById(@Argument Long id) {
        return transactionService.getTransactionById(id);
    }
    
    @QueryMapping
    public List<Transaction> getTransactionsByAccountId(@Argument Long accountId) {
        return transactionService.getTransactionsByAccountId(accountId);
    }
    
    @QueryMapping
    public List<Transaction> getTransactionsByDateRange(@Argument Long accountId, 
                                                        @Argument String startDate, 
                                                        @Argument String endDate) {
        return transactionService.getTransactionsByDateRange(accountId, startDate, endDate);
    }
    
    @QueryMapping
    public AccountStatement getAccountStatement(@Argument String accountNumber, 
                                                @Argument String startDate, 
                                                @Argument String endDate) {
        return transactionService.getAccountStatement(accountNumber, startDate, endDate);
    }
}
