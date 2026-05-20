CREATE TABLE reservations (
                              id UUID PRIMARY KEY,

                              user_id UUID NOT NULL,
                              ticket_type_id UUID NOT NULL,

                              quantity INTEGER NOT NULL,
                              expires_at TIMESTAMP NOT NULL,
                              status VARCHAR(30) NOT NULL,

                              created_at TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP,
                              version BIGINT,

                              CONSTRAINT fk_reservations_user
                                  FOREIGN KEY (user_id)
                                      REFERENCES users(id),

                              CONSTRAINT fk_reservations_ticket_type
                                  FOREIGN KEY (ticket_type_id)
                                      REFERENCES ticket_types(id)
);

CREATE INDEX idx_reservations_user_id
    ON reservations(user_id);
