CREATE TABLE users (
                       id UUID PRIMARY KEY,

                       email VARCHAR(150) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(30) NOT NULL,
                       status VARCHAR(30) NOT NULL,

                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP,
                       version BIGINT
);

CREATE INDEX idx_users_email
    ON users(email);