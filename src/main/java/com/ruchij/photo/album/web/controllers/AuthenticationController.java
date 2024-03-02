package com.ruchij.photo.album.web.controllers;


import com.ruchij.photo.album.daos.user.User;
import com.ruchij.photo.album.web.controllers.requests.LoginRequest;
import com.ruchij.photo.album.web.controllers.responses.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {
	private final AuthenticationManager authenticationManager;
	private final SecurityContextRepository securityContextRepository;
	private final LogoutHandler logoutHandler;

	public AuthenticationController(
		AuthenticationManager authenticationManager,
		SecurityContextRepository securityContextRepository,
		LogoutHandler logoutHandler
	) {
		this.authenticationManager = authenticationManager;
		this.securityContextRepository = securityContextRepository;
		this.logoutHandler = logoutHandler;
	}

	@ResponseBody
	@PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse login(
		@Valid @RequestBody LoginRequest loginRequest,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse
	) {
		Authentication authentication =
			authenticationManager.authenticate(
				UsernamePasswordAuthenticationToken.unauthenticated(
					loginRequest.email(),
					loginRequest.password()
				)
			);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		securityContextRepository.saveContext(context, httpServletRequest, httpServletResponse);

		return UserResponse.from((User) authentication.getPrincipal());
	}

	@GetMapping("/current")
	public ResponseEntity<UserResponse> current(@AuthenticationPrincipal User user) {
		if (user == null) {
			throw new AuthenticationCredentialsNotFoundException("No authenticated user found");
		} else {
			return ResponseEntity.ok(UserResponse.from(user));
		}
	}

	@DeleteMapping("/logout")
	public ResponseEntity<UserResponse> logout(
		@AuthenticationPrincipal User user,
		Authentication authentication,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse
	) {
		if (user == null) {
			throw new AuthenticationCredentialsNotFoundException("No authenticated user found");
		} else {
			logoutHandler.logout(httpServletRequest, httpServletResponse, authentication);
			return ResponseEntity.ok(UserResponse.from(user));
		}
	}
}
