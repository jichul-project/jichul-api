CREATE TABLE users
(
    id         uuid      DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT pk_users
            PRIMARY KEY,
    email      VARCHAR(255)                        NOT NULL
        CONSTRAINT uq_users_email
            UNIQUE,
    password   VARCHAR(255)                        NOT NULL,
    name       VARCHAR(100)                        NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()             NOT NULL,
    allow      BOOLEAN   DEFAULT FALSE
);

