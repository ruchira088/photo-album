package com.ruchij.photo.album.web.controllers.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Optional;

public record CreateUserRequest(
	@Email String email,
	@Size(min = 6, message = "password has to be greater than 6 characters") String password,
	@NotBlank(message = "firstName CANNOT be blank") String firstName,
	Optional<String> lastName
) {
}
