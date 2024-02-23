CREATE TABLE api_user
(
    id         VARCHAR(63) UNIQUE  NOT NULL,
    created_at TIMESTAMP           NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255)        NOT NULL,
    last_name  VARCHAR(255),
    PRIMARY KEY (id)
);


CREATE TABLE credentials
(
    user_id  VARCHAR(63)  NOT NULL UNIQUE,
    password VARCHAR(127) NOT NULL UNIQUE,

    PRIMARY KEY (user_id),
    CONSTRAINT fk_credentials_user_id FOREIGN KEY (user_id) REFERENCES api_user (id)
);