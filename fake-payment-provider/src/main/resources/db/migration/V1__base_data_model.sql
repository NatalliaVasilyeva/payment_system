CREATE TABLE IF NOT EXISTS merchant
(
    id            uuid PRIMARY KEY,
    client_id     VARCHAR(16) NOT NULL UNIQUE,
    client_secret bytea       NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS transaction
(
    id                      uuid PRIMARY KEY,
    merchant_id             uuid            NOT NULL UNIQUE,
    payment_method          varchar(64)     NOT NULL UNIQUE,
    amount                  numeric(10, 20) NOT NULL,
    currency                varchar(16)     NOT NULL UNIQUE,
    merchant_transaction_id uuid            NOT NULL UNIQUE,
    created_at              timestamp with time zone,
    updated_at              timestamp with time zone,
    card_number             varchar(16)     NOT NULL UNIQUE,
    card_expirationDate     varchar(10)     NOT NULL,
    card_cvv                bytea           NOT NULL,
    language                varchar(64)     NOT NULL,
    customer_first_name     varchar(64)     NOT NULL,
    customer_last_name      varchar(64)     NOT NULL,
    customer_country        varchar(64)     NOT NULL,
    transactional           boolean         NOT NULL,
    status                  varchar(16)     NOT NULL,
    message                 text,

    CONSTRAINT fk_transaction_merchant_id
        FOREIGN KEY(merchant_id)
            REFERENCES merchant(id)
);

CREATE TABLE IF NOT EXISTS wallet
(
    id          uuid PRIMARY KEY,
    merchant_id uuid    NOT NULL,
    balance     numeric NOT NULL DEFAULT 0,

    CONSTRAINT fk_wallet_merchant_id
        FOREIGN KEY(merchant_id)
            REFERENCES merchant(id)
);

CREATE TABLE IF NOT EXISTS webhook
(
    id                      uuid PRIMARY KEY,
    merchant_transaction_id uuid         NOT NULL UNIQUE,
    notification_url        VARCHAR(128) NOT NULL,
    status                  VARCHAR(16)  NOT NULL
);

-- Index
CREATE INDEX IF NOT EXISTS webhook_merchant_transaction_id ON webhook (merchant_transaction_id);
CREATE INDEX IF NOT EXISTS transaction_merchant_transaction_id ON transaction (merchant_transaction_id);
CREATE INDEX IF NOT EXISTS idx_webhook_still_in_progress ON webhook (status) WHERE status IN ('IN_PROGRESS');