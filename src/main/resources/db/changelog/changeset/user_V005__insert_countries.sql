INSERT INTO country (title)
VALUES
    ('United States'),
    ('United Kingdom'),
    ('Australia'),
    ('France')
    ON CONFLICT (title) DO NOTHING;