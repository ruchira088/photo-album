package com.ruchij.photo.album.web.controllers.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record LoginRequest(
	@Email String email,
	@Size(min = 6, message = "password has to be greater than 6 characters") String password
) {
}
