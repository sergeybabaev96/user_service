ALTER TABLE users
ADD COLUMN if not exists locale VARCHAR(8) NOT NULL DEFAULT 'en-US';