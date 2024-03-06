package com.ruchij.photo.album.daos.photo;

import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.resource.ResourceFile;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Optional;

@Entity(name = "photo")
public final class Photo {
	@Id
	@Column(name = "id", nullable = false, unique = true, updatable = false)
	private String id;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@ManyToOne
	@JoinColumn(name = "album_id")
	private Album album;

	@Column(name = "title")
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "width", nullable = false)
	private Integer width;

	@Column(name = "height", nullable = false)
	private Integer height;

	@OneToOne(optional = false)
	@JoinColumn(name = "resource_file_id", nullable = false)
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

	public Optional<String> getTitle() {
		return Optional.ofNullable(title);
	}

	public void setTitle(Optional<String> title) {
		this.title = title.orElse(null);
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(description);
	}

	public void setDescription(Optional<String> description) {
		this.description = description.orElse(null);
	}

	public ResourceFile getResourceFile() {
		return resourceFile;
	}

	public void setResourceFile(ResourceFile resourceFile) {
		this.resourceFile = resourceFile;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}
}
