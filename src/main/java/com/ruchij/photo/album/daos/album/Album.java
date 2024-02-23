package com.ruchij.photo.album.daos.album;

import com.ruchij.photo.album.daos.user.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Optional;

@Entity(name = "album")
public final class Album {
	@Id
	@Column(name = "id", nullable = false, unique = true, updatable = false)
	private String id;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "is_public", nullable = false)
	private Boolean isPublic;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "album")
	private AlbumPassword albumPassword;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(description);
	}

	public void setDescription(Optional<String> description) {
		this.description = description.orElse(null);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Boolean getPublic() {
		return isPublic;
	}

	public void setPublic(Boolean aPublic) {
		isPublic = aPublic;
	}

	public AlbumPassword getAlbumPassword() {
		return albumPassword;
	}

	public void setAlbumPassword(AlbumPassword albumPassword) {
		this.albumPassword = albumPassword;
	}
}
