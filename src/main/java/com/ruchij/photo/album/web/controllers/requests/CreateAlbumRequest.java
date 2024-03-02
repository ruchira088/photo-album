package com.ruchij.photo.album.web.controllers.requests;

import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public record CreateAlbumRequest(
	@NotBlank(message = "name cannot be blank")
	String name,
	Optional<String> description,
	Boolean isPublic,
	Optional<String> password
) {
}
