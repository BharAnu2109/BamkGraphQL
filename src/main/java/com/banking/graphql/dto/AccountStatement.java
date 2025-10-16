package com.banking.graphql.dto;

import com.banking.graphql.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatement {
    private String accountNumber;
    private String startDate;
    private String endDate;
    private Double openingBalance;
    private Double closingBalance;
    private Double totalDeposits;
    private Double totalWithdrawals;
    private Integer transactionCount;
    private List<Transaction> transactions;
}
