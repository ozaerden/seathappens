ALTER TABLE notifications
    ADD COLUMN reference_id UUID,
    ADD COLUMN retry_count INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN last_error TEXT,
    ADD COLUMN next_retry_at TIMESTAMP;

CREATE INDEX idx_notifications_status_next_retry_at_created_at
    ON notifications(status, next_retry_at, created_at);
