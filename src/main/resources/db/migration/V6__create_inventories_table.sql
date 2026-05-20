CREATE TABLE inventories (
                             id UUID PRIMARY KEY,

                             ticket_type_id UUID NOT NULL UNIQUE,

                             total_quantity INTEGER NOT NULL,
                             available_quantity INTEGER NOT NULL,
                             reserved_quantity INTEGER NOT NULL,
                             sold_quantity INTEGER NOT NULL,

                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP,
                             version BIGINT,

                             CONSTRAINT fk_inventories_ticket_type
                                 FOREIGN KEY (ticket_type_id)
                                     REFERENCES ticket_types(id)
);