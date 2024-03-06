package com.ruchij.photo.album.daos.album;

import jakarta.persistence.*;

@Entity(name = "album_password")
public final class AlbumPassword {
	@Id
	@OneToOne
	@JoinColumn(name = "album_id")
	private Album album;

	@Column(name = "hashed_password", nullable = false)
	private String hashedPassword;

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}
}
