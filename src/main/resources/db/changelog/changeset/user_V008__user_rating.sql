CREATE TABLE rating_types
(
    id       bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name     varchar(64) NOT NULL UNIQUE,
    cost     smallint    NOT NULL,
    activity boolean
);

CREATE TABLE user_rating
(
    id      bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,
    type_id bigint NOT NULL,
    score   bigint,

    CONSTRAINT fk_user_rating_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_type_id FOREIGN KEY (type_id) REFERENCES rating_types (id)
);