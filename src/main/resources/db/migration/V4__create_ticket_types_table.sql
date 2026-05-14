CREATE TABLE ticket_types (
                              id UUID PRIMARY KEY,
                              event_id UUID NOT NULL,

                              name VARCHAR(100) NOT NULL,
                              description VARCHAR(500),
                              price NUMERIC(10, 2) NOT NULL,
                              total_quantity INTEGER NOT NULL,
                              status VARCHAR(30) NOT NULL,

                              created_at TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP,
                              version BIGINT,

                              CONSTRAINT fk_ticket_types_event
                                  FOREIGN KEY (event_id)
                                      REFERENCES events(id)
);