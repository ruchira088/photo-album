package com.ruchij.photo.album.web.exceptions;

import com.ruchij.photo.album.services.exceptions.AuthorizationException;
import com.ruchij.photo.album.services.exceptions.ResourceConflictException;
import com.ruchij.photo.album.services.exceptions.ResourceNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ApplicationExceptionHandler {
	@ExceptionHandler({ResourceNotFoundException.class, ResourceConflictException.class, AuthorizationException.class, AuthenticationException.class})
	private ResponseEntity<ErrorMessage> customExceptionHandler(Exception exception) {
		HttpStatus httpStatus;

		if (exception instanceof ResourceNotFoundException) {
			httpStatus = HttpStatus.NOT_FOUND;
		} else if (exception instanceof ResourceConflictException) {
			httpStatus = HttpStatus.CONFLICT;
		} else if (exception instanceof AuthenticationException) {
			httpStatus = HttpStatus.UNAUTHORIZED;
		} else if (exception instanceof AuthorizationException) {
			httpStatus = HttpStatus.FORBIDDEN;
		} else {
			httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
		}

		return ResponseEntity
			.status(httpStatus)
			.body(new ErrorMessage(List.of(exception.getMessage())));
	}

	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<ErrorMessage> validationExceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException) {
		return ResponseEntity
			.badRequest()
			.body(
				new ErrorMessage(
					methodArgumentNotValidException.getAllErrors().stream()
						.map(DefaultMessageSourceResolvable::getDefaultMessage)
						.toList())
			);
	}
}
