# Automated Transaction Batch Processor

A Core Java and JDBC project that bulk-inserts banking transactions into MySQL using JDBC batching. It uses no Spring Boot and no JPA. The goal is to show how much batching improves insert performance compared with row-by-row inserts.

## Tech stack

- Java 21
- JDBC (`PreparedStatement`, `addBatch()` / `executeBatch()`)
- MySQL 8+
- Maven

## Project structure

```
src/main/java/com/banking/batch/
├── Transaction.java      # Model: id, accountId, amount, type, status
├── DatabaseConfig.java   # MySQL connection setup (env-based config)
├── BatchProcessor.java   # Batched and non-batched insert logic
└── Main.java             # Generates 1,000 mock transactions and times both approaches
schema.sql                # Database and table creation script
```

## How it works

- Transactions are inserted in chunks of 500 using `addBatch()` and `executeBatch()`.
- Auto-commit is disabled, and the whole load is committed once, so it is all-or-nothing. On failure the transaction is rolled back.
- The JDBC URL uses `rewriteBatchedStatements=true`, which lets the MySQL driver merge each batch into multi-row `INSERT` statements.
- Connections and statements are managed with try-with-resources.
- Credentials are read from environment variables, so no secrets live in source control.

## Setup

1. **Create the database and table** by running `schema.sql` in MySQL Workbench or the MySQL CLI.

2. **Set your credentials** (PowerShell):

   ```powershell
   $env:DB_USER = "root"
   $env:DB_PASSWORD = "your_mysql_password"
   ```

   Optionally override the full URL with `DB_URL`. Defaults are `root` and `jdbc:mysql://localhost:3306/banking_db`.

3. **Run the project:**

   ```powershell
   mvn compile exec:java
   ```

## Benchmark

Inserting 1,000 transactions on a local MySQL instance:

| Approach | Time |
|---|---|
| Row-by-row inserts (auto-commit) | ~3,080 ms |
| JDBC batching (batch size 500, single commit) | ~100 ms |

That is roughly a 30x improvement. Batching cuts network round trips and disk flushes. Results are from a single run on a local machine and will vary.

## Possible improvements

- Use a connection pool such as HikariCP
- Stream transactions from a file or queue instead of an in-memory list
- Handle partial failures by catching `BatchUpdateException`
- Tune the batch size by measuring
- Add unit and integration tests
