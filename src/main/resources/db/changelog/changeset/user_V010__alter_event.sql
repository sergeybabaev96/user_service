ALTER TABLE event
ADD COLUMN tariff_id bigint,
    ADD CONSTRAINT fk_tariff_event_id FOREIGN KEY (tariff_id) REFERENCES tariff (id)