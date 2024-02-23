package com.ruchij.photo.album.web.controllers.responses;

import com.ruchij.photo.album.daos.user.User;

public record UserResponse(String userId, String email, String firstName, String lastName) {
	public static UserResponse from(User user) {
		return new UserResponse(
			user.getId(),
			user.getEmail(),
			user.getFirstName(),
			user.getLastName().orElse(null)
		);
	}
}
