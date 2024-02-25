package com.ruchij.photo.album.services.exceptions;

public class AuthorizationException extends RuntimeException {
	public AuthorizationException(String errorMessage) {
		super(errorMessage);
	}
}
