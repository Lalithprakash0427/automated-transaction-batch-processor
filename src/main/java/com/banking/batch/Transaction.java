package com.banking.batch;

import java.math.BigDecimal;

public class Transaction {

    public enum Type { DEPOSIT, WITHDRAWAL, TRANSFER }

    public enum Status { PENDING, COMPLETED, FAILED }

    private final String id;
    private final long accountId;
    private final BigDecimal amount;
    private final Type type;
    private final Status status;

    public Transaction(String id, long accountId, BigDecimal amount, Type type, Status status) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }

    public String getId() { return id; }

    public long getAccountId() { return accountId; }

    public BigDecimal getAmount() { return amount; }

    public Type getType() { return type; }

    public Status getStatus() { return status; }

    @Override
    public String toString() {
        return "Transaction{id='" + id + "', accountId=" + accountId +
                ", amount=" + amount + ", type=" + type + ", status=" + status + '}';
    }
}