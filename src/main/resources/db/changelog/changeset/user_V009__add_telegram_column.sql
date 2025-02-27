ALTER TABLE users
    ADD COLUMN telegram_username varchar,
    ADD COLUMN telegram_chat_id varchar,
    ADD COLUMN preference INT;