package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.daos.user.User;
import com.ruchij.photo.album.services.exceptions.ResourceConflictException;
import com.ruchij.photo.album.services.user.UserService;
import com.ruchij.photo.album.web.controllers.requests.CreateUserRequest;
import com.ruchij.photo.album.web.controllers.responses.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse create(@Valid @RequestBody CreateUserRequest createUserRequest) throws ResourceConflictException {

		User user = userService.create(
			createUserRequest.email(),
			createUserRequest.password(),
			createUserRequest.firstName(),
			createUserRequest.lastName()
		);

		return UserResponse.from(user);
	}
}
