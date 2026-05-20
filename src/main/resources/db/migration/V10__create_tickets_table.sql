CREATE TABLE tickets (
                         id UUID PRIMARY KEY,

                         order_id UUID NOT NULL,
                         ticket_type_id UUID NOT NULL,

                         ticket_code VARCHAR(64) NOT NULL UNIQUE,
                         status VARCHAR(30) NOT NULL,

                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP,
                         version BIGINT,

                         CONSTRAINT fk_tickets_order
                             FOREIGN KEY (order_id)
                                 REFERENCES orders(id),

                         CONSTRAINT fk_tickets_ticket_type
                             FOREIGN KEY (ticket_type_id)
                                 REFERENCES ticket_types(id)
);