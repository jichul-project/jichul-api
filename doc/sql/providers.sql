-- auto-generated definition
CREATE TABLE providers
(
    id         uuid      DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT pk_providers
            PRIMARY KEY,
    user_id    uuid                                NOT NULL
        CONSTRAINT fk_providers_user
            REFERENCES users
            ON DELETE CASCADE,
    name       VARCHAR(100)                        NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT uq_providers_user_name
        UNIQUE (user_id, name)
);

CREATE INDEX idx_providers_user_id
    ON providers (user_id);

