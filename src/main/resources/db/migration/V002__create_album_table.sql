CREATE TABLE album
(
    id          VARCHAR(63),
    user_id     VARCHAR(63)   NOT NULL,
    created_at  TIMESTAMP     NOT NULL,
    name        VARCHAR(127)  NOT NULL,
    description VARCHAR(2047) NULL,
    is_public   BOOLEAN       NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_album_user_id FOREIGN KEY (user_id) REFERENCES api_user (id)
);

CREATE TABLE album_password
(
    album_id VARCHAR(63),
    hashed_password VARCHAR(127) NOT NULL,

    PRIMARY KEY (album_id),
    CONSTRAINT fk_album_password_album_id FOREIGN KEY (album_id) REFERENCES album (id)
);