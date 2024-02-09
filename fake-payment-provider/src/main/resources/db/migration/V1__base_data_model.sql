CREATE TABLE IF NOT EXISTS merchant
(
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id     varchar(64) NOT NULL UNIQUE,
    client_secret varchar     NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS transaction
(
    id                      uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id             uuid           NOT NULL,
    payment_method          varchar(64)    NOT NULL,
    amount                  numeric(10, 2) NOT NULL,
    currency                varchar(16)    NOT NULL,
    merchant_transaction_id uuid           NOT NULL UNIQUE,
    created_at              timestamp with time zone,
    updated_at              timestamp with time zone,
    notification_url        varchar(128)   NOT NULL,
    card_number             varchar(16)    NOT NULL,
    card_expiration_date    varchar(10),
    card_cvv                integer         ,
    language                varchar(64)    NOT NULL,
    customer_first_name     varchar(64)    NOT NULL,
    customer_last_name      varchar(64)    NOT NULL,
    customer_country        varchar(64)    NOT NULL,
    type                    varchar(64)    NOT NULL,
    status                  varchar(16)    NOT NULL,
    message                 text,

    CONSTRAINT fk_transaction_merchant_id
        FOREIGN KEY (merchant_id)
            REFERENCES merchant (id)
);

CREATE TABLE IF NOT EXISTS wallet
(
    id          uuid PRIMARY KEY        DEFAULT gen_random_uuid(),
    merchant_id uuid           NOT NULL,
    currency    varchar(16)    NOT NULL,
    balance     numeric(10, 2) NOT NULL DEFAULT 0,

    CONSTRAINT fk_wallet_merchant_id
        FOREIGN KEY (merchant_id)
            REFERENCES merchant (id)
);

CREATE TABLE IF NOT EXISTS webhook
(
    id                      uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id             uuid           NOT NULL,
    merchant_transaction_id uuid           NOT NULL,
    provider_transaction_id uuid           NOT NULL,
    notification_url        VARCHAR(128)   NOT NULL,
    payment_method          varchar(64)    NOT NULL,
    amount                  numeric(10, 2) NOT NULL,
    currency                varchar(16)    NOT NULL,
    created_at              timestamp with time zone,
    updated_at              timestamp with time zone,
    type                    varchar(64)    NOT NULL,
    card_number             varchar(16)    NOT NULL,
    language                varchar(64)    NOT NULL,
    customer_first_name     varchar(64)    NOT NULL,
    customer_last_name      varchar(64)    NOT NULL,
    status                  varchar(16)    NOT NULL,
    message                 text,
    attempt                 int NOT NULL,
    notification_result     varchar(16) NOT NULL,

    UNIQUE(provider_transaction_id, attempt),

    CONSTRAINT fk_webhook_merchant_id
        FOREIGN KEY (merchant_id)
            REFERENCES merchant (id)
);

-- Index
CREATE INDEX IF NOT EXISTS webhook_merchant_transaction_id ON webhook (merchant_transaction_id);
CREATE INDEX IF NOT EXISTS transaction_merchant_transaction_id ON transaction (merchant_transaction_id);
CREATE INDEX IF NOT EXISTS idx_webhook_still_in_progress ON webhook (status) WHERE status IN ('IN_PROGRESS');
CREATE INDEX IF NOT EXISTS idx_transaction_still_in_progress ON webhook (status) WHERE status IN ('IN_PROGRESS');