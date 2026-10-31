CREATE DATABASE IF NOT EXISTS banking_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE banking_db;

CREATE TABLE IF NOT EXISTS transactions (
                                            id          VARCHAR(36)    NOT NULL,
    account_id  BIGINT         NOT NULL,
    amount      DECIMAL(15, 2) NOT NULL,
    type        ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL,
    status      ENUM('PENDING', 'COMPLETED', 'FAILED')    NOT NULL,
    created_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_transactions_account_id (account_id)
    ) ENGINE = InnoDB;