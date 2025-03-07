INSERT INTO country (title) VALUES ('Russia'), ('USA');

INSERT INTO users (username, email, phone, password, active, about_me, country_id, city, experience, created_at, updated_at, profile_pic_file_id)
VALUES
    ('JohnDoe', 'johndoe@example.com', '1234567890', 'password1', true, 'About John Doe', 1, 'Moscow', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'http://user.image/1'),
    ('JaneSmith', 'janesmith@example.com', '0987654321', 'password2', true, 'About Jane Smith', 2, 'New York', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'http://user.image/2');
