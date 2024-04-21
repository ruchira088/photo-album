CREATE TABLE usage
(
    user_id         VARCHAR(63) UNIQUE NOT NULL,
    last_updated_at TIMESTAMP          NOT NULL,
    photo_count     INT                NOT NULL,
    album_count     INT                NOT NULL,
    bytes_used      BIGINT             NOT NULL,

    PRIMARY KEY (user_id),
    CONSTRAINT fk_usage_user_id FOREIGN KEY (user_id) REFERENCES api_user (id)
)