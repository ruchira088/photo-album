CREATE TABLE resource_file
(
    id           VARCHAR(63),
    name         VARCHAR(127) NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    content_type VARCHAR(63)  NOT NULL,
    file_key     VARCHAR(255) NOT NULL,
    file_size    BIGINT       NOT NULL,

    PRIMARY KEY (id)
);