package com.ruchij.photo.album.dao.album;

import java.time.Instant;
import java.util.Optional;

public record Album(String id, Instant createdAt, String userId, String name, Optional<String> description) {
}
