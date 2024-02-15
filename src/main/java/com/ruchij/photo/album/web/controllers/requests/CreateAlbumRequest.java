package com.ruchij.photo.album.web.controllers.requests;

import java.util.Optional;

public record CreateAlbumRequest(String name, Optional<String> description) {
}
