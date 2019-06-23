package com.meb.vo;

import java.time.LocalDateTime;

/**
 * This represents a single transaction record.
 * The fields in this class correspond to the fields in the transaction file
 * 
 * @author sundeep
 *
 */
public class TransactionRecord {

    public enum TransactionType {
        PAYMENT,
        REVERSAL;
    }
    
    private String transactionId;
    private String fromAccountId;
    private String toAccountId;
    private LocalDateTime createdAt;
    private Double amount;
    private TransactionType txnType;
    private String origTransactionid;
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getFromAccountId() {
        return fromAccountId;
    }
    
    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }
    
    public String getToAccountId() {
        return toAccountId;
    }
    
    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public TransactionType getTxnType() {
        return txnType;
    }
    
    public void setTxnType(TransactionType txnType) {
        this.txnType = txnType;
    }
    
    public String getOrigTransactionid() {
        return origTransactionid;
    }
    
    public void setOrigTransactionid(String origTransactionid) {
        this.origTransactionid = origTransactionid;
    }
    
    public String toString() {
        String s = String.format("Txn id: [%s], From a/c: [%s], To a/c: [%s], Created: [%s], Amount: [%.2f], Type: [%s], Orig Txn: [%s]", 
                                 transactionId, fromAccountId, toAccountId, createdAt, amount, txnType, origTransactionid);
        return s;
    }
}
