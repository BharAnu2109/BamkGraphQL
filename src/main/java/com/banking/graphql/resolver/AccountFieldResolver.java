package com.banking.graphql.resolver;

import com.banking.graphql.model.Account;
import com.banking.graphql.model.Customer;
import com.banking.graphql.model.Transaction;
import com.banking.graphql.service.CustomerService;
import com.banking.graphql.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AccountFieldResolver {
    
    private final CustomerService customerService;
    private final TransactionService transactionService;
    
    @SchemaMapping(typeName = "Account", field = "customer")
    public Customer getCustomer(Account account) {
        return customerService.getCustomerById(account.getCustomer().getId());
    }
    
    @SchemaMapping(typeName = "Account", field = "transactions")
    public List<Transaction> getTransactions(Account account) {
        return transactionService.getTransactionsByAccountId(account.getId());
    }
    
    @SchemaMapping(typeName = "Account", field = "customerId")
    public Long getCustomerId(Account account) {
        return account.getCustomer().getId();
    }
}
