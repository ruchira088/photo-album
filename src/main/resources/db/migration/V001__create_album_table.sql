CREATE TABLE album
(
    id          VARCHAR(63),
    created_at  TIMESTAMP     NOT NULL,
    name        VARCHAR(127)  NOT NULL,
    description VARCHAR(2047) NULL,

    PRIMARY KEY (id)
);