package com.ruchij.photo.album.web.controller.requests;

import java.util.Optional;

public record CreateAlbumRequest(String name, Optional<String> description) {
}
