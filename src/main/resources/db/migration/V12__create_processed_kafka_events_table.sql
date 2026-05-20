CREATE TABLE processed_kafka_events (
        id UUID PRIMARY KEY,

        event_id UUID NOT NULL UNIQUE,
        event_type VARCHAR(100) NOT NULL,
        topic VARCHAR(255) NOT NULL,
        consumer_group VARCHAR(255) NOT NULL,

        processed_at TIMESTAMP NOT NULL,

        created_at TIMESTAMP NOT NULL,
        updated_at TIMESTAMP,
        version BIGINT
);

CREATE INDEX idx_processed_kafka_events_event_type
    ON processed_kafka_events(event_type);