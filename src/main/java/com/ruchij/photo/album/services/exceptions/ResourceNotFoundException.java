package com.ruchij.photo.album.services.exceptions;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String id, Class<?> clazz) {
		super("Unable to find %s with id=%s".formatted(clazz.getSimpleName(), id));
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

}
