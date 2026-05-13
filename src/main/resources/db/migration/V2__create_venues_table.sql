CREATE TABLE venues (
                        id UUID PRIMARY KEY,
                        name VARCHAR(150) NOT NULL,
                        city VARCHAR(100) NOT NULL,
                        country VARCHAR(100) NOT NULL,
                        capacity INTEGER NOT NULL,
                        status VARCHAR(30) NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP,
                        version BIGINT
);