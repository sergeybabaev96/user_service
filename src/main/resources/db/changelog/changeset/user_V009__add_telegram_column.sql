ALTER TABLE users
    ADD COLUMN telegram_username varchar(64),
    ADD COLUMN telegram_chat_id varchar(64),
    ADD COLUMN preference INT;