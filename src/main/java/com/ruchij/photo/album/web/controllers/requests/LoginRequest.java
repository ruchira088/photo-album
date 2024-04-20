package com.ruchij.photo.album.web.controllers.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record LoginRequest(
	@Email(message = "Email must be a valid email address") String email,
	@Size(min = 6, message = "Password must be greater than 6 characters") String password
) {
}
