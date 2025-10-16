package com.banking.graphql.service;

import com.banking.graphql.exception.InvalidAccountException;
import com.banking.graphql.exception.ResourceNotFoundException;
import com.banking.graphql.model.Account;
import com.banking.graphql.model.AccountStatus;
import com.banking.graphql.model.AccountType;
import com.banking.graphql.model.Customer;
import com.banking.graphql.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final CustomerService customerService;
    
    public List<Account> getAllAccounts() {
        log.info("Fetching all accounts");
        return accountRepository.findAll();
    }
    
    public Account getAccountById(Long id) {
        log.info("Fetching account with id: {}", id);
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }
    
    public Account getAccountByAccountNumber(String accountNumber) {
        log.info("Fetching account with account number: {}", accountNumber);
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));
    }
    
    public List<Account> getAccountsByCustomerId(Long customerId) {
        log.info("Fetching accounts for customer id: {}", customerId);
        return accountRepository.findByCustomerId(customerId);
    }
    
    public Double getAccountBalance(String accountNumber) {
        log.info("Fetching balance for account: {}", accountNumber);
        Account account = getAccountByAccountNumber(accountNumber);
        return account.getBalance();
    }
    
    @Transactional
    public Account createAccount(Long customerId, AccountType accountType, Double initialDeposit, String currency) {
        log.info("Creating new account for customer id: {} with type: {}", customerId, accountType);
        
        if (initialDeposit < 0) {
            throw new InvalidAccountException("Initial deposit cannot be negative");
        }
        
        Customer customer = customerService.getCustomerById(customerId);
        
        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountType(accountType);
        account.setBalance(initialDeposit);
        account.setCurrency(currency != null ? currency : "USD");
        account.setStatus(AccountStatus.ACTIVE);
        
        return accountRepository.save(account);
    }
    
    @Transactional
    public Account updateAccount(Long id, AccountStatus status) {
        log.info("Updating account with id: {}", id);
        
        Account account = getAccountById(id);
        
        if (status != null) {
            account.setStatus(status);
        }
        
        return accountRepository.save(account);
    }
    
    @Transactional
    public boolean closeAccount(Long id) {
        log.info("Closing account with id: {}", id);
        
        Account account = getAccountById(id);
        
        if (account.getBalance() > 0) {
            throw new InvalidAccountException("Cannot close account with positive balance. Please withdraw all funds first.");
        }
        
        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
        return true;
    }
    
    @Transactional
    public void updateBalance(Account account, Double newBalance) {
        account.setBalance(newBalance);
        accountRepository.save(account);
    }
    
    public void validateAccountForTransaction(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountException("Account is not active. Current status: " + account.getStatus());
        }
    }
}
