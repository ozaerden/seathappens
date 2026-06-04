ALTER TABLE outbox_events
    ADD COLUMN correlation_id VARCHAR(100);

CREATE INDEX idx_outbox_events_correlation_id
    ON outbox_events(correlation_id);
