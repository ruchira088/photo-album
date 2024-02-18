package com.ruchij.photo.album.services.models;

import java.util.stream.Stream;

public record HealthCheck(Status database, Status storage) {
	public boolean isHealthy() {
		return Stream.of(database, storage).allMatch(status -> status == Status.UP);
	}

	public enum Status {
		UP, DOWN
	}
}