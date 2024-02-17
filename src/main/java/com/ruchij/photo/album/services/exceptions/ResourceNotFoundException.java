package com.ruchij.photo.album.services.exceptions;

public class ResourceNotFoundException extends RuntimeException {
	private final String id;
	private final Class<?> clazz;

	public ResourceNotFoundException(String id, Class<?> clazz) {
		this.id = id;
		this.clazz = clazz;
	}

	@Override
	public String getMessage() {
		return "Unable to find %s with id=%s".formatted(clazz.getSimpleName(), id);
	}
}
