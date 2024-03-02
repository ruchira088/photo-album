package com.ruchij.photo.album.web.controllers.requests;

import jakarta.validation.constraints.NotBlank;

public record AuthenticateAlbumRequest(@NotBlank(message = "password CANNOT be blank") String password) {
}
