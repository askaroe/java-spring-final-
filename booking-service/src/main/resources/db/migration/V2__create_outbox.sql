-- Create outbox_events table for Kafka outbox pattern
CREATE TABLE IF NOT EXISTS outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(100),
    aggregate_id UUID,
    event_type VARCHAR(100),
    payload TEXT,
    status VARCHAR(20),
    created_at TIMESTAMP,
    processed_at TIMESTAMP
);

