package com.ruchij.photo.album.web.exceptions;

import com.ruchij.photo.album.services.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ApplicationExceptionHandler {

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorMessage> authenticationException(AuthenticationException authenticationException) {
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(new ErrorMessage(List.of(authenticationException.getMessage())));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	private ResponseEntity<ErrorMessage> resourceNotFoundException(ResourceNotFoundException resourceNotFoundException) {
		return ResponseEntity
			.status(HttpStatus.NOT_FOUND)
			.body(new ErrorMessage(List.of(resourceNotFoundException.getMessage())));
	}
}
