CREATE TABLE photo
(
    id               VARCHAR(63),
    created_at       TIMESTAMP     NOT NULL,
    album_id         VARCHAR(63)   NOT NULL,
    user_id          VARCHAR(63)   NOT NULL,
    title            VARCHAR(127)  NULL,
    description      VARCHAR(2047) NULL,
    resource_file_id VARCHAR(63)   NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_photo_album_id FOREIGN KEY (album_id) REFERENCES album (id),
    CONSTRAINT fk_photo_resource_file_id FOREIGN KEY (resource_file_id) REFERENCES resource_file (id)
);