CREATE TABLE orders (
                        id UUID PRIMARY KEY,

                        user_id UUID NOT NULL,
                        reservation_id UUID NOT NULL UNIQUE,

                        total_amount NUMERIC(10, 2) NOT NULL,
                        status VARCHAR(30) NOT NULL,

                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP,
                        version BIGINT,

                        CONSTRAINT fk_orders_user
                            FOREIGN KEY (user_id)
                                REFERENCES users(id),

                        CONSTRAINT fk_orders_reservation
                            FOREIGN KEY (reservation_id)
                                REFERENCES reservations(id)
);

CREATE INDEX idx_orders_user_id
    ON orders(user_id);