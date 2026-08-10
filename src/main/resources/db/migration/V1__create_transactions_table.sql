CREATE TABLE transactions(
    id UUID PRIMARY KEY,
    account_id VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL ,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    provider_transaction_id VARCHAR(100),
    balance_after NUMERIC(19,2),
    error_message VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
--
-- INDEX FOR FILTERS(account_id, status and type) supports the optional filters of the GET/transactions query endpoint efficiently at scale.

--
-- FILTER PER ACCOUNT_ID
CREATE INDEX idx_transactions_account_id
     on transactions(account_id);

--
-- FILTER PER STATUS
CREATE INDEX idx_transactions_status
     on transactions(status);


--
--FILTER PER CREATED_AT
CREATE INDEX idx_transactions_created_at
    on transactions(created_at);

--
--FILTER PER TYPE
CREATE INDEX idx_transactions_type
    ON transactions(type);


SELECT *
FROM flyway_schema_history;

SELECT *
FROM flyway_schema_history
ORDER BY installed_rank;

SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public';

SELECT *
FROM transactions;

SELECT version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;