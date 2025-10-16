package com.banking.graphql.service;

import com.banking.graphql.dto.AccountStatement;
import com.banking.graphql.dto.TransferResult;
import com.banking.graphql.exception.InsufficientFundsException;
import com.banking.graphql.exception.InvalidAccountException;
import com.banking.graphql.model.*;
import com.banking.graphql.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    public List<Transaction> getAllTransactions() {
        log.info("Fetching all transactions");
        return transactionRepository.findAll();
    }
    
    public Transaction getTransactionById(Long id) {
        log.info("Fetching transaction with id: {}", id);
        return transactionRepository.findById(id)
                .orElseThrow(() -> new InvalidAccountException("Transaction not found with id: " + id));
    }
    
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        log.info("Fetching transactions for account id: {}", accountId);
        return transactionRepository.findByAccountId(accountId);
    }
    
    public List<Transaction> getTransactionsByDateRange(Long accountId, String startDate, String endDate) {
        log.info("Fetching transactions for account id: {} between {} and {}", accountId, startDate, endDate);
        
        LocalDateTime start = LocalDateTime.parse(startDate, DATE_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endDate, DATE_FORMATTER);
        
        return transactionRepository.findByAccountIdAndDateRange(accountId, start, end);
    }
    
    public AccountStatement getAccountStatement(String accountNumber, String startDate, String endDate) {
        log.info("Generating account statement for account: {} from {} to {}", accountNumber, startDate, endDate);
        
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        LocalDateTime start = LocalDateTime.parse(startDate, DATE_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endDate, DATE_FORMATTER);
        
        List<Transaction> transactions = transactionRepository.findByAccountIdAndDateRange(
            account.getId(), start, end
        );
        
        double totalDeposits = 0.0;
        double totalWithdrawals = 0.0;
        
        for (Transaction txn : transactions) {
            if (txn.getTransactionType() == TransactionType.DEPOSIT || 
                txn.getTransactionType() == TransactionType.TRANSFER_IN ||
                txn.getTransactionType() == TransactionType.INTEREST_CREDIT) {
                totalDeposits += txn.getAmount();
            } else if (txn.getTransactionType() == TransactionType.WITHDRAWAL || 
                       txn.getTransactionType() == TransactionType.TRANSFER_OUT ||
                       txn.getTransactionType() == TransactionType.FEE_DEBIT) {
                totalWithdrawals += txn.getAmount();
            }
        }
        
        Double openingBalance = transactions.isEmpty() ? account.getBalance() : 
                                transactions.get(transactions.size() - 1).getBalanceAfter() - 
                                (transactions.get(transactions.size() - 1).getTransactionType() == TransactionType.DEPOSIT ? 
                                 transactions.get(transactions.size() - 1).getAmount() : 
                                 -transactions.get(transactions.size() - 1).getAmount());
        
        return AccountStatement.builder()
                .accountNumber(accountNumber)
                .startDate(startDate)
                .endDate(endDate)
                .openingBalance(openingBalance)
                .closingBalance(account.getBalance())
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .transactionCount(transactions.size())
                .transactions(transactions)
                .build();
    }
    
    @Transactional
    public Transaction deposit(String accountNumber, Double amount, String description) {
        log.info("Processing deposit of {} to account: {}", amount, accountNumber);
        
        if (amount <= 0) {
            throw new InvalidAccountException("Deposit amount must be positive");
        }
        
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        accountService.validateAccountForTransaction(account);
        
        Double newBalance = account.getBalance() + amount;
        
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setCurrency(account.getCurrency());
        transaction.setDescription(description != null ? description : "Deposit");
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setBalanceAfter(newBalance);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        accountService.updateBalance(account, newBalance);
        
        log.info("Deposit completed. New balance: {}", newBalance);
        return savedTransaction;
    }
    
    @Transactional
    public Transaction withdraw(String accountNumber, Double amount, String description) {
        log.info("Processing withdrawal of {} from account: {}", amount, accountNumber);
        
        if (amount <= 0) {
            throw new InvalidAccountException("Withdrawal amount must be positive");
        }
        
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        accountService.validateAccountForTransaction(account);
        
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds. Current balance: " + account.getBalance());
        }
        
        Double newBalance = account.getBalance() - amount;
        
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setCurrency(account.getCurrency());
        transaction.setDescription(description != null ? description : "Withdrawal");
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setBalanceAfter(newBalance);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        accountService.updateBalance(account, newBalance);
        
        log.info("Withdrawal completed. New balance: {}", newBalance);
        return savedTransaction;
    }
    
    @Transactional
    public TransferResult transfer(String sourceAccountNumber, String destinationAccountNumber, 
                                   Double amount, String description) {
        log.info("Processing transfer of {} from {} to {}", amount, sourceAccountNumber, destinationAccountNumber);
        
        if (amount <= 0) {
            throw new InvalidAccountException("Transfer amount must be positive");
        }
        
        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            throw new InvalidAccountException("Cannot transfer to the same account");
        }
        
        Account sourceAccount = accountService.getAccountByAccountNumber(sourceAccountNumber);
        Account destinationAccount = accountService.getAccountByAccountNumber(destinationAccountNumber);
        
        accountService.validateAccountForTransaction(sourceAccount);
        accountService.validateAccountForTransaction(destinationAccount);
        
        if (sourceAccount.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds in source account. Current balance: " + sourceAccount.getBalance());
        }
        
        // Create source transaction (debit)
        Double sourceNewBalance = sourceAccount.getBalance() - amount;
        Transaction sourceTransaction = new Transaction();
        sourceTransaction.setAccount(sourceAccount);
        sourceTransaction.setTransactionType(TransactionType.TRANSFER_OUT);
        sourceTransaction.setAmount(amount);
        sourceTransaction.setCurrency(sourceAccount.getCurrency());
        sourceTransaction.setDescription(description != null ? description : "Transfer to " + destinationAccountNumber);
        sourceTransaction.setStatus(TransactionStatus.COMPLETED);
        sourceTransaction.setBalanceAfter(sourceNewBalance);
        
        Transaction savedSourceTransaction = transactionRepository.save(sourceTransaction);
        accountService.updateBalance(sourceAccount, sourceNewBalance);
        
        // Create destination transaction (credit)
        Double destNewBalance = destinationAccount.getBalance() + amount;
        Transaction destTransaction = new Transaction();
        destTransaction.setAccount(destinationAccount);
        destTransaction.setTransactionType(TransactionType.TRANSFER_IN);
        destTransaction.setAmount(amount);
        destTransaction.setCurrency(destinationAccount.getCurrency());
        destTransaction.setDescription(description != null ? description : "Transfer from " + sourceAccountNumber);
        destTransaction.setStatus(TransactionStatus.COMPLETED);
        destTransaction.setBalanceAfter(destNewBalance);
        destTransaction.setRelatedTransactionId(savedSourceTransaction.getId());
        
        Transaction savedDestTransaction = transactionRepository.save(destTransaction);
        accountService.updateBalance(destinationAccount, destNewBalance);
        
        // Update source transaction with related transaction ID
        savedSourceTransaction.setRelatedTransactionId(savedDestTransaction.getId());
        transactionRepository.save(savedSourceTransaction);
        
        log.info("Transfer completed successfully");
        
        return TransferResult.builder()
                .success(true)
                .message("Transfer completed successfully")
                .sourceTransaction(savedSourceTransaction)
                .destinationTransaction(savedDestTransaction)
                .build();
    }
    
    @Transactional
    public List<TransferResult> bulkTransfer(List<BulkTransferRequest> transfers) {
        log.info("Processing bulk transfer of {} transactions", transfers.size());
        
        List<TransferResult> results = new ArrayList<>();
        
        for (BulkTransferRequest request : transfers) {
            try {
                TransferResult result = transfer(
                    request.getSourceAccountNumber(),
                    request.getDestinationAccountNumber(),
                    request.getAmount(),
                    request.getDescription()
                );
                results.add(result);
            } catch (Exception e) {
                log.error("Error processing transfer: {}", e.getMessage());
                results.add(TransferResult.builder()
                        .success(false)
                        .message("Transfer failed: " + e.getMessage())
                        .build());
            }
        }
        
        return results;
    }
    
    // Inner class for bulk transfer request
    public static class BulkTransferRequest {
        private String sourceAccountNumber;
        private String destinationAccountNumber;
        private Double amount;
        private String description;
        
        public BulkTransferRequest(String sourceAccountNumber, String destinationAccountNumber, 
                                   Double amount, String description) {
            this.sourceAccountNumber = sourceAccountNumber;
            this.destinationAccountNumber = destinationAccountNumber;
            this.amount = amount;
            this.description = description;
        }
        
        public String getSourceAccountNumber() {
            return sourceAccountNumber;
        }
        
        public String getDestinationAccountNumber() {
            return destinationAccountNumber;
        }
        
        public Double getAmount() {
            return amount;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
