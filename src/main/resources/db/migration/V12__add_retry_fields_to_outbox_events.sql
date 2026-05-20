ALTER TABLE outbox_events
    ADD COLUMN last_error TEXT,
    ADD COLUMN next_retry_at TIMESTAMP;

CREATE INDEX idx_outbox_events_status_next_retry_at_created_at
    ON outbox_events(status, next_retry_at, created_at);