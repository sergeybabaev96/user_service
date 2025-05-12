ALTER TABLE outbox_message
    ALTER COLUMN payload TYPE TEXT USING payload::TEXT;