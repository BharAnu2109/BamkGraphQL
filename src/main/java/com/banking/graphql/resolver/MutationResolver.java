package com.banking.graphql.resolver;

import com.banking.graphql.dto.TransferResult;
import com.banking.graphql.model.*;
import com.banking.graphql.service.AccountService;
import com.banking.graphql.service.CustomerService;
import com.banking.graphql.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MutationResolver {
    
    private final CustomerService customerService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    
    // Customer Mutations
    @MutationMapping
    public Customer createCustomer(@Argument Map<String, Object> input) {
        Customer customer = new Customer();
        customer.setFirstName((String) input.get("firstName"));
        customer.setLastName((String) input.get("lastName"));
        customer.setEmail((String) input.get("email"));
        customer.setPhone((String) input.get("phone"));
        customer.setAddress((String) input.get("address"));
        customer.setDateOfBirth(LocalDate.parse((String) input.get("dateOfBirth"), DATE_FORMATTER));
        
        return customerService.createCustomer(customer);
    }
    
    @MutationMapping
    public Customer updateCustomer(@Argument Long id, @Argument Map<String, Object> input) {
        Customer customerDetails = new Customer();
        
        if (input.containsKey("firstName")) {
            customerDetails.setFirstName((String) input.get("firstName"));
        }
        if (input.containsKey("lastName")) {
            customerDetails.setLastName((String) input.get("lastName"));
        }
        if (input.containsKey("email")) {
            customerDetails.setEmail((String) input.get("email"));
        }
        if (input.containsKey("phone")) {
            customerDetails.setPhone((String) input.get("phone"));
        }
        if (input.containsKey("address")) {
            customerDetails.setAddress((String) input.get("address"));
        }
        
        return customerService.updateCustomer(id, customerDetails);
    }
    
    @MutationMapping
    public Boolean deleteCustomer(@Argument Long id) {
        return customerService.deleteCustomer(id);
    }
    
    // Account Mutations
    @MutationMapping
    public Account createAccount(@Argument Map<String, Object> input) {
        Long customerId = Long.valueOf(input.get("customerId").toString());
        AccountType accountType = AccountType.valueOf((String) input.get("accountType"));
        Double initialDeposit = Double.valueOf(input.get("initialDeposit").toString());
        String currency = input.containsKey("currency") ? (String) input.get("currency") : "USD";
        
        return accountService.createAccount(customerId, accountType, initialDeposit, currency);
    }
    
    @MutationMapping
    public Account updateAccount(@Argument Long id, @Argument Map<String, Object> input) {
        AccountStatus status = null;
        if (input.containsKey("status")) {
            status = AccountStatus.valueOf((String) input.get("status"));
        }
        
        return accountService.updateAccount(id, status);
    }
    
    @MutationMapping
    public Boolean closeAccount(@Argument Long id) {
        return accountService.closeAccount(id);
    }
    
    // Transaction Mutations
    @MutationMapping
    public Transaction deposit(@Argument Map<String, Object> input) {
        String accountNumber = (String) input.get("accountNumber");
        Double amount = Double.valueOf(input.get("amount").toString());
        String description = input.containsKey("description") ? (String) input.get("description") : null;
        
        return transactionService.deposit(accountNumber, amount, description);
    }
    
    @MutationMapping
    public Transaction withdraw(@Argument Map<String, Object> input) {
        String accountNumber = (String) input.get("accountNumber");
        Double amount = Double.valueOf(input.get("amount").toString());
        String description = input.containsKey("description") ? (String) input.get("description") : null;
        
        return transactionService.withdraw(accountNumber, amount, description);
    }
    
    @MutationMapping
    public TransferResult transfer(@Argument Map<String, Object> input) {
        String sourceAccountNumber = (String) input.get("sourceAccountNumber");
        String destinationAccountNumber = (String) input.get("destinationAccountNumber");
        Double amount = Double.valueOf(input.get("amount").toString());
        String description = input.containsKey("description") ? (String) input.get("description") : null;
        
        return transactionService.transfer(sourceAccountNumber, destinationAccountNumber, amount, description);
    }
    
    // Complex Banking Operations
    @MutationMapping
    public Account openSavingsAccountWithDeposit(@Argument Long customerId, @Argument Double initialDeposit) {
        log.info("Opening savings account with initial deposit for customer: {}", customerId);
        return accountService.createAccount(customerId, AccountType.SAVINGS, initialDeposit, "USD");
    }
    
    @MutationMapping
    public List<TransferResult> bulkTransfer(@Argument List<Map<String, Object>> transfers) {
        log.info("Processing bulk transfer with {} transactions", transfers.size());
        
        List<TransactionService.BulkTransferRequest> requests = transfers.stream()
                .map(transfer -> new TransactionService.BulkTransferRequest(
                        (String) transfer.get("sourceAccountNumber"),
                        (String) transfer.get("destinationAccountNumber"),
                        Double.valueOf(transfer.get("amount").toString()),
                        transfer.containsKey("description") ? (String) transfer.get("description") : null
                ))
                .collect(Collectors.toList());
        
        return transactionService.bulkTransfer(requests);
    }
}
