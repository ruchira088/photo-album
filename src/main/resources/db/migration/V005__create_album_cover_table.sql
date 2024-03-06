CREATE TABLE album_cover
(
    album_id         VARCHAR(63) UNIQUE NOT NULL,
    created_at       TIMESTAMP          NOT NULL,
    width            INT                NOT NULL,
    height           INT                NOT NULL,
    resource_file_id VARCHAR(63) UNIQUE NOT NULL,

    PRIMARY KEY (album_id),
    CONSTRAINT fk_album_cover_album_id FOREIGN KEY (album_id) REFERENCES album (id),
    CONSTRAINT fk_album_cover_resource_file_id FOREIGN KEY (resource_file_id) REFERENCES resource_file (id)
);