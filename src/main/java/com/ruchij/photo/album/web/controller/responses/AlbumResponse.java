package com.ruchij.photo.album.web.controller.responses;

import java.time.Instant;

public record AlbumResponse(String id, Instant createdAt, String userId, String name, String description) {
}
