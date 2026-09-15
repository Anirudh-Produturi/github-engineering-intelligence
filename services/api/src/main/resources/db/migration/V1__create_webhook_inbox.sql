CREATE SCHEMA IF NOT EXISTS integration;

CREATE TABLE integration.webhook_delivery (
    delivery_id       varchar(128) PRIMARY KEY,
    event_type        varchar(128) NOT NULL,
    payload            jsonb NOT NULL,
    received_at        timestamptz NOT NULL DEFAULT now(),
    processing_status varchar(32) NOT NULL DEFAULT 'PENDING',
    attempt_count      integer NOT NULL DEFAULT 0,
    processed_at       timestamptz,
    last_error         text,
    CONSTRAINT webhook_delivery_status_check
      CHECK (processing_status IN ('PENDING', 'PROCESSING', 'PROCESSED', 'FAILED', 'DEAD_LETTER'))
);

CREATE INDEX webhook_delivery_pending_idx
    ON integration.webhook_delivery (received_at)
    WHERE processing_status = 'PENDING';

