CREATE TABLE bookings (
                          id              UUID PRIMARY KEY,
                          user_id         UUID NOT NULL,
                          event_id        UUID NOT NULL,
                          status          VARCHAR(32) NOT NULL,
                          total_amount    NUMERIC(10,2) NOT NULL,
                          currency        VARCHAR(16) NOT NULL,
                          payment_status  VARCHAR(32) NOT NULL,
                          payment_ref     VARCHAR(255),
                          created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
                          updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE booking_items (
                               id              UUID PRIMARY KEY,
                               booking_id      UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
                               ticket_type_id  UUID NOT NULL,
                               quantity        INT NOT NULL,
                               unit_price      NUMERIC(10,2) NOT NULL,
                               total_price     NUMERIC(10,2) NOT NULL
);

CREATE TABLE outbox_events (
                               id              UUID PRIMARY KEY,
                               aggregate_type  VARCHAR(64) NOT NULL,
                               aggregate_id    UUID NOT NULL,
                               event_type      VARCHAR(64) NOT NULL,
                               payload         JSONB NOT NULL,
                               status          VARCHAR(32) NOT NULL,
                               created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
                               processed_at    TIMESTAMP WITHOUT TIME ZONE
);
