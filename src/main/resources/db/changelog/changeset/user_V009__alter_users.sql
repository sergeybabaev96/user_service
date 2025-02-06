ALTER TABLE users
ADD COLUMN tariff_id bigint,
    ADD CONSTRAINT fk_tariff_user_id FOREIGN KEY (tariff_id) REFERENCES tariff (id)