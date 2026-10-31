package com.banking.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class BatchProcessor {

    private static final String INSERT_SQL =
            "INSERT INTO transactions (id, account_id, amount, type, status) VALUES (?, ?, ?, ?, ?)";

    private static final int BATCH_SIZE = 500;

    /**
     * Inserts all transactions in chunks of BATCH_SIZE inside a single database
     * transaction. Either every row is saved or none are.
     *
     * @return number of transactions submitted for insertion
     */
    public int processBatch(List<Transaction> transactions) throws SQLException {
        if (transactions == null || transactions.isEmpty()) {
            return 0;
        }

        int processed = 0;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {

            connection.setAutoCommit(false);

            try {
                int count = 0;

                for (Transaction tx : transactions) {
                    statement.setString(1, tx.getId());
                    statement.setLong(2, tx.getAccountId());
                    statement.setBigDecimal(3, tx.getAmount());
                    statement.setString(4, tx.getType().name());
                    statement.setString(5, tx.getStatus().name());
                    statement.addBatch();
                    count++;

                    if (count % BATCH_SIZE == 0) {
                        statement.executeBatch();
                        processed += BATCH_SIZE;
                    }
                }

                // Flush the remaining rows that didn't fill a complete batch
                int remaining = count % BATCH_SIZE;
                if (remaining > 0) {
                    statement.executeBatch();
                    processed += remaining;
                }

                connection.commit();

            } catch (SQLException e) {
                rollbackQuietly(connection);
                throw e;
            }
        }

        return processed;
    }
    public int processWithoutBatch(List<Transaction> transactions) throws SQLException {
        int processed = 0;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {

            for (Transaction tx : transactions) {   // autocommit stays ON
                statement.setString(1, tx.getId());
                statement.setLong(2, tx.getAccountId());
                statement.setBigDecimal(3, tx.getAmount());
                statement.setString(4, tx.getType().name());
                statement.setString(5, tx.getStatus().name());
                statement.executeUpdate();
                processed++;
            }
        }
        return processed;
    }

    private void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
            System.err.println("Batch failed. Transaction rolled back.");
        } catch (SQLException rollbackEx) {
            System.err.println("Rollback failed: " + rollbackEx.getMessage());
        }
    }
}