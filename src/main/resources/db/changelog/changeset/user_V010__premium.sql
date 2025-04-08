ALTER TABLE users
    ADD COLUMN IF NOT EXISTS premium_active DEFAULT false;

ALTER TABLE user_premium
    ADD COALESCE IF NOT EXISTS auto_renew DEFAULT false;