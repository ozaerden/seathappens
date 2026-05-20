CREATE TABLE payments (
                          id UUID PRIMARY KEY,

                          order_id UUID NOT NULL UNIQUE,

                          amount NUMERIC(10, 2) NOT NULL,
                          status VARCHAR(30) NOT NULL,
                          provider VARCHAR(30) NOT NULL,

                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP,
                          version BIGINT,

                          CONSTRAINT fk_payments_order
                              FOREIGN KEY (order_id)
                                  REFERENCES orders(id)
);