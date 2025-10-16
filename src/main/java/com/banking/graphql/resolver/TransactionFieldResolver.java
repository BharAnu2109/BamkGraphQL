package com.banking.graphql.resolver;

import com.banking.graphql.model.Account;
import com.banking.graphql.model.Transaction;
import com.banking.graphql.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class TransactionFieldResolver {
    
    private final AccountService accountService;
    
    @SchemaMapping(typeName = "Transaction", field = "account")
    public Account getAccount(Transaction transaction) {
        return accountService.getAccountById(transaction.getAccount().getId());
    }
    
    @SchemaMapping(typeName = "Transaction", field = "accountId")
    public Long getAccountId(Transaction transaction) {
        return transaction.getAccount().getId();
    }
}
