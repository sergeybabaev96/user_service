CREATE TABLE tariff (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    plan varchar(64) NOT NULL,
    shows int DEFAULT 0 NOT NULL,
    priority int NOT NULL,
    active bool DEFAULT FALSE NOT NULL,
    user_id bigint,
    event_id bigint,
    payment_id bigint,
    expire_period timestamptz,

    CONSTRAINT fk_user_tariff_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_event_tariff_id FOREIGN KEY (event_id) REFERENCES event (id)
);
