package com.banking.graphql.resolver;

import com.banking.graphql.model.Account;
import com.banking.graphql.model.Customer;
import com.banking.graphql.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerFieldResolver {
    
    private final AccountService accountService;
    
    @SchemaMapping(typeName = "Customer", field = "accounts")
    public List<Account> getAccounts(Customer customer) {
        return accountService.getAccountsByCustomerId(customer.getId());
    }
}
