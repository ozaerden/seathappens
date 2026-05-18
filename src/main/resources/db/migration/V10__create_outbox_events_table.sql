CREATE TABLE outbox_events (
                               id UUID PRIMARY KEY,

                               aggregate_type VARCHAR(100) NOT NULL,
                               aggregate_id VARCHAR(255) NOT NULL,
                               event_type VARCHAR(100) NOT NULL,
                               payload TEXT NOT NULL,

                               status VARCHAR(30) NOT NULL,
                               published_at TIMESTAMP,
                               retry_count INTEGER NOT NULL,

                               created_at TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP,
                               version BIGINT
);

CREATE INDEX idx_outbox_events_status_created_at
    ON outbox_events(status, created_at);