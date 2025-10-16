package com.banking.graphql.config;

import com.banking.graphql.model.*;
import com.banking.graphql.repository.AccountRepository;
import com.banking.graphql.repository.CustomerRepository;
import com.banking.graphql.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    
    @Override
    public void run(String... args) {
        log.info("Initializing sample banking data...");
        
        // Create customers
        Customer customer1 = createCustomer("John", "Doe", "john.doe@example.com", 
                                           "+1-555-0101", "123 Main St, New York, NY", 
                                           LocalDate.of(1985, 5, 15));
        
        Customer customer2 = createCustomer("Jane", "Smith", "jane.smith@example.com", 
                                           "+1-555-0102", "456 Oak Ave, Los Angeles, CA", 
                                           LocalDate.of(1990, 8, 22));
        
        Customer customer3 = createCustomer("Robert", "Johnson", "robert.j@example.com", 
                                           "+1-555-0103", "789 Pine Rd, Chicago, IL", 
                                           LocalDate.of(1978, 3, 10));
        
        // Create accounts
        Account account1 = createAccount(customer1, AccountType.SAVINGS, 5000.0);
        Account account2 = createAccount(customer1, AccountType.CHECKING, 2500.0);
        Account account3 = createAccount(customer2, AccountType.SAVINGS, 10000.0);
        Account account4 = createAccount(customer3, AccountType.CURRENT, 15000.0);
        
        // Create sample transactions
        createTransaction(account1, TransactionType.DEPOSIT, 5000.0, 
                         "Initial deposit", 5000.0);
        createTransaction(account2, TransactionType.DEPOSIT, 2500.0, 
                         "Initial deposit", 2500.0);
        createTransaction(account3, TransactionType.DEPOSIT, 10000.0, 
                         "Initial deposit", 10000.0);
        createTransaction(account4, TransactionType.DEPOSIT, 15000.0, 
                         "Initial deposit", 15000.0);
        
        log.info("Sample data initialization completed!");
        log.info("Created {} customers", customerRepository.count());
        log.info("Created {} accounts", accountRepository.count());
        log.info("Created {} transactions", transactionRepository.count());
    }
    
    private Customer createCustomer(String firstName, String lastName, String email, 
                                    String phone, String address, LocalDate dob) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);
        customer.setDateOfBirth(dob);
        return customerRepository.save(customer);
    }
    
    private Account createAccount(Customer customer, AccountType accountType, Double balance) {
        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountType(accountType);
        account.setBalance(balance);
        account.setCurrency("USD");
        account.setStatus(AccountStatus.ACTIVE);
        return accountRepository.save(account);
    }
    
    private Transaction createTransaction(Account account, TransactionType type, 
                                         Double amount, String description, Double balanceAfter) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setCurrency(account.getCurrency());
        transaction.setDescription(description);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setBalanceAfter(balanceAfter);
        return transactionRepository.save(transaction);
    }
}
