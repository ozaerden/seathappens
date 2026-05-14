CREATE TABLE events (
                        id UUID PRIMARY KEY,

                        venue_id UUID NOT NULL,

                        name VARCHAR(200) NOT NULL,
                        description VARCHAR(2000),

                        start_date_time TIMESTAMP NOT NULL,
                        end_date_time TIMESTAMP NOT NULL,

                        status VARCHAR(30) NOT NULL,

                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP,
                        version BIGINT,

                        CONSTRAINT fk_events_venue
                            FOREIGN KEY (venue_id)
                                REFERENCES venues(id)
);