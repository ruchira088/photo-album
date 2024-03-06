package com.ruchij.photo.album.daos.album;

import com.ruchij.photo.album.daos.resource.ResourceFile;
import jakarta.persistence.*;

import java.time.Instant;

@Entity(name = "album_cover")
public final class AlbumCover {
	@Id
	@OneToOne
	@JoinColumn(name = "album_id", nullable = false)
	private Album album;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "width", nullable = false)
	private Integer width;

	@Column(name = "height", nullable = false)
	private Integer height;

	@OneToOne
	@JoinColumn(name = "resource_file_id", nullable = false)
	private ResourceFile resourceFile;

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
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

	public ResourceFile getResourceFile() {
		return resourceFile;
	}

	public void setResourceFile(ResourceFile resourceFile) {
		this.resourceFile = resourceFile;
	}
}
