ALTER TABLE contact_preferences rename COLUMN preference TO preference_old;
ALTER TABLE contact_preferences ADD COLUMN preference VARCHAR(32);
UPDATE contact_preferences
SET preference = CASE
                       WHEN CAST(preference_old AS INTEGER) = 0 THEN 'EMAIL'
                       WHEN CAST(preference_old AS INTEGER) = 1 THEN 'PHONE'
                       WHEN CAST(preference_old AS INTEGER) = 2 THEN 'TELEGRAM'
                       END;
ALTER TABLE contact_preferences ALTER COLUMN preference SET NOT NULL;
ALTER TABLE contact_preferences DROP COLUMN preference_old;