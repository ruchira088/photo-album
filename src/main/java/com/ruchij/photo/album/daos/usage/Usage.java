package com.ruchij.photo.album.daos.usage;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;

@Entity(name = "usage")
public final class Usage implements Serializable {
	@Id
	@Column(name = "user_id", updatable = false, nullable = false)
	private String userId;

	@Column(name = "last_updated_at", updatable = false, nullable = false)
	private Instant lastUpdatedAt;

	@Column(name = "photo_count")
	private int photoCount;

	@Column(name = "album_count")
	private int albumCount;

	@Column(name = "bytes_used")
	private long bytesUsed;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Instant getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public void setLastUpdatedAt(Instant lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public int getPhotoCount() {
		return photoCount;
	}

	public void setPhotoCount(int photoCount) {
		this.photoCount = photoCount;
	}

	public int getAlbumCount() {
		return albumCount;
	}

	public void setAlbumCount(int albumCount) {
		this.albumCount = albumCount;
	}

	public long getBytesUsed() {
		return bytesUsed;
	}

	public void setBytesUsed(long dataUsed) {
		this.bytesUsed = dataUsed;
	}
}
