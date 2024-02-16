package com.ruchij.photo.album.daos.photo;

import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.resource.ResourceFile;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Optional;

@Entity(name = "photo")
public class Photo {
	@Id
	@Column(name = "id", nullable = false, unique = true, updatable = false)
	private String id;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@ManyToOne(optional = false)
	@JoinColumn(name = "album_id", nullable = false)
	private Album album;

	@Column(name = "user_id", nullable = false, updatable = false)
	private String userId;

	@Column(name = "title")
	private String title;

	@Column(name = "description")
	private String description;

	@OneToOne(optional = false)
	@JoinColumn(name = "resource_id", nullable = false)
	private ResourceFile resourceFile;

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

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Optional<String> getMaybeTitle() {
		return Optional.ofNullable(title);
	}

	public void setMaybeTitle(Optional<String> maybeTitle) {
		this.title = maybeTitle.orElse(null);
	}

	public Optional<String> getMaybeDescription() {
		return Optional.ofNullable(description);
	}

	public void setMaybeDescription(Optional<String> maybeDescription) {
		this.description = maybeDescription.orElse(null);
	}

	public ResourceFile getResourceFile() {
		return resourceFile;
	}

	public void setResourceFile(ResourceFile resourceFile) {
		this.resourceFile = resourceFile;
	}
}
