ALTER TABLE contact_preferences rename COLUMN preference TO preference_old;
ALTER TABLE contact_preferences ADD COLUMN preference VARCHAR(32);
UPDATE contact_preferences
SET preference = CASE preference_old WHEN 0 THEN 'EMAIL'
                                     WHEN 1 THEN 'PHONE'
                                     WHEN 2 THEN 'TELEGRAM'
                                 END;
ALTER TABLE contact_preferences ALTER COLUMN preference SET NOT NULL;
ALTER TABLE contact_preferences DROP COLUMN preference_old;