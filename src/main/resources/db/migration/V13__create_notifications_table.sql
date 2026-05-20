CREATE TABLE notifications (
                               id UUID PRIMARY KEY,

                               event_id UUID NOT NULL UNIQUE,
                               event_type VARCHAR(100) NOT NULL,

                               recipient VARCHAR(255) NOT NULL,
                               subject VARCHAR(255) NOT NULL,
                               content TEXT NOT NULL,
                               status VARCHAR(30) NOT NULL,

                               created_at TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP,
                               version BIGINT
);

CREATE INDEX idx_notifications_event_type
    ON notifications(event_type);