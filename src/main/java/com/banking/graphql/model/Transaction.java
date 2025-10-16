package com.banking.graphql.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(nullable = false)
    private String currency = "USD";
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.COMPLETED;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Column(nullable = false, unique = true)
    private String referenceNumber;
    
    @Column(nullable = false)
    private Double balanceAfter;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private Long relatedTransactionId;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        generateReferenceNumber();
    }
    
    private void generateReferenceNumber() {
        if (referenceNumber == null) {
            referenceNumber = "TXN" + System.currentTimeMillis() + ((int) (Math.random() * 10000));
        }
    }
}
