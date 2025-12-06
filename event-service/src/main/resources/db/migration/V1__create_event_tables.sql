CREATE TABLE venues (
                        id          UUID PRIMARY KEY,
                        name        VARCHAR(255) NOT NULL,
                        address     VARCHAR(255),
                        city        VARCHAR(100),
                        capacity    INT,
                        created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
                        updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE events (
                        id           UUID PRIMARY KEY,
                        organizer_id VARCHAR(255) NOT NULL,
                        venue_id     UUID NOT NULL REFERENCES venues(id) ON DELETE CASCADE,
                        title        VARCHAR(255) NOT NULL,
                        description  TEXT,
                        category     VARCHAR(64) NOT NULL,
                        start_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                        end_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                        status       VARCHAR(32) NOT NULL,
                        created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
                        updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE ticket_types (
                              id                  UUID PRIMARY KEY,
                              event_id            UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
                              name                VARCHAR(100) NOT NULL,
                              price               NUMERIC(10,2) NOT NULL,
                              currency            VARCHAR(16) NOT NULL,
                              total_quantity      INT NOT NULL,
                              remaining_quantity  INT NOT NULL,
                              created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
                              updated_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);
