package com.ruchij.photo.album.daos.album;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.Optional;

@Entity(name = "album")
public final class Album {
	@Id
	@Column(name = "id", nullable = false, unique = true, updatable = false)
	private String id;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "user_id", nullable = false, updatable = false)
	private String userId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	public Album(String id, Instant createdAt, String userId, String name, Optional<String> maybeDescription) {
		setId(id);
		setCreatedAt(createdAt);
		setUserId(userId);
		setName(name);
		setMaybeDescription(maybeDescription);
	}

	public Album() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Optional<String> getMaybeDescription() {
		return Optional.ofNullable(description);
	}

	public void setMaybeDescription(Optional<String> maybeDescription) {
		this.description = maybeDescription.orElse(null);
	}
}
