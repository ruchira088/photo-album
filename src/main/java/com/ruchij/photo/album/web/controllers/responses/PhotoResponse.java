package com.ruchij.photo.album.web.controllers.responses;

import com.ruchij.photo.album.daos.photo.Photo;

import java.util.Optional;

public record PhotoResponse(String id, String albumId, Optional<String> title, Optional<String> description) {
	public static PhotoResponse from(Photo photo) {
		return new PhotoResponse(photo.getId(), photo.getAlbum().getId(), photo.getTitle(), photo.getDescription());
	}
}
