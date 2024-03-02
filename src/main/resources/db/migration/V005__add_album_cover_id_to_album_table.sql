
ALTER TABLE album ADD COLUMN album_cover_id VARCHAR(63) NULL
    CONSTRAINT fk_album_album_cover_id REFERENCES resource_file (id);