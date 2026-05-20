CREATE TABLE orders (
                        id UUID PRIMARY KEY,

                        reservation_id UUID NOT NULL UNIQUE,

                        total_amount NUMERIC(10, 2) NOT NULL,
                        status VARCHAR(30) NOT NULL,

                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP,
                        version BIGINT,

                        CONSTRAINT fk_orders_reservation
                            FOREIGN KEY (reservation_id)
                                REFERENCES reservations(id)
);