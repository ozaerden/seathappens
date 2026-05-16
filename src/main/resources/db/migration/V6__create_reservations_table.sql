CREATE TABLE reservations (
                              id UUID PRIMARY KEY,

                              ticket_type_id UUID NOT NULL,

                              quantity INTEGER NOT NULL,
                              expires_at TIMESTAMP NOT NULL,
                              status VARCHAR(30) NOT NULL,

                              created_at TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP,
                              version BIGINT,

                              CONSTRAINT fk_reservations_ticket_type
                                  FOREIGN KEY (ticket_type_id)
                                      REFERENCES ticket_types(id)
);