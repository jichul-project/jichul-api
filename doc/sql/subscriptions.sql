CREATE TABLE subscriptions
(
    id          uuid        DEFAULT gen_random_uuid()        NOT NULL
        CONSTRAINT pk_subscriptions
            PRIMARY KEY,
    user_id     uuid                                         NOT NULL
        CONSTRAINT fk_subscriptions_user
            REFERENCES users
            ON DELETE CASCADE,
    provider_id uuid                                         NOT NULL
        CONSTRAINT fk_subscriptions_provider
            REFERENCES providers,
    name        VARCHAR(200)                                 NOT NULL,
    amount      NUMERIC(12, 2)                               NOT NULL,
    type        VARCHAR(10)                                  NOT NULL
        CONSTRAINT subscriptions_type_check
            CHECK ((type)::TEXT = ANY (ARRAY [('MONTHLY'::CHARACTER VARYING)::TEXT, ('YEARLY'::CHARACTER VARYING)::TEXT])),
    price_type  VARCHAR(10) DEFAULT 'WON'::CHARACTER VARYING NOT NULL
        CONSTRAINT subscriptions_price_type_check
            CHECK ((price_type)::TEXT = ANY (ARRAY [('WON'::CHARACTER VARYING)::TEXT, ('DOLLAR'::CHARACTER VARYING)::TEXT])),
    description VARCHAR(500),
    created_at  TIMESTAMP   DEFAULT NOW(),
    updated_at  TIMESTAMP   DEFAULT NOW()
);

CREATE INDEX idx_subscriptions_user_id
    ON subscriptions (user_id);

CREATE INDEX idx_subscriptions_provider_id
    ON subscriptions (provider_id);

