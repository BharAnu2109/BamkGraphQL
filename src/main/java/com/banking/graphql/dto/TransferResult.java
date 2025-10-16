package com.banking.graphql.dto;

import com.banking.graphql.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResult {
    private Boolean success;
    private String message;
    private Transaction sourceTransaction;
    private Transaction destinationTransaction;
}
