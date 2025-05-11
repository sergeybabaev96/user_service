CREATE TABLE IF NOT EXISTS outbox_message
(
    id            BIGSERIAL PRIMARY KEY,
    event_id      UUID         NOT NULL,
    event_type    VARCHAR(255) NOT NULL,
    payload       JSONB        NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    published_at  TIMESTAMP,
    partition_key VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_outbox_message_unpublished ON outbox_message (created_at)
WHERE published_at IS NULL;