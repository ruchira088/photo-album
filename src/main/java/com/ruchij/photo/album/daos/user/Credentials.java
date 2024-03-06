package com.ruchij.photo.album.daos.user;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "credentials")
public final class Credentials implements Serializable {
	@Id
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "password", unique = true, updatable = false, nullable = false)
	private String password;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
