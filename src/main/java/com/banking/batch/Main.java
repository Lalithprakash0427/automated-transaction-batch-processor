package com.banking.batch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Main {

    private static final int TOTAL_TRANSACTIONS = 1_000;

    public static void main(String[] args) {
        BatchProcessor processor = new BatchProcessor();

        // Separate lists, because each transaction has a unique ID
        List<Transaction> singleList = generateMockTransactions(TOTAL_TRANSACTIONS);
        List<Transaction> batchList = generateMockTransactions(TOTAL_TRANSACTIONS);

        try {
            // 1. One-by-one inserts (no batching, commit per row)
            long start = System.nanoTime();
            processor.processWithoutBatch(singleList);
            long singleMs = (System.nanoTime() - start) / 1_000_000;
            System.out.println("Without batching: " + singleMs + " ms");

            // 2. Batched inserts
            start = System.nanoTime();
            int inserted = processor.processBatch(batchList);
            long batchMs = (System.nanoTime() - start) / 1_000_000;
            System.out.println("With batching:    " + batchMs + " ms (" + inserted + " rows)");

        } catch (SQLException e) {
            System.err.println("Processing failed: " + e.getMessage());
        }
    }

    private static List<Transaction> generateMockTransactions(int count) {
        List<Transaction> list = new ArrayList<>(count);
        Random random = new Random();

        Transaction.Type[] types = Transaction.Type.values();
        Transaction.Status[] statuses = Transaction.Status.values();

        for (int i = 0; i < count; i++) {
            String id = UUID.randomUUID().toString();
            long accountId = 100_000L + random.nextInt(900);
            BigDecimal amount = BigDecimal
                    .valueOf(1 + random.nextDouble() * 9_999)
                    .setScale(2, RoundingMode.HALF_UP);
            Transaction.Type type = types[random.nextInt(types.length)];
            Transaction.Status status = statuses[random.nextInt(statuses.length)];

            list.add(new Transaction(id, accountId, amount, type, status));
        }
        return list;
    }
}