package com.ruchij.photo.album.web.controllers.responses;

import com.ruchij.photo.album.daos.album.Album;

import java.util.Optional;

public record AlbumResponse(String id, String userId, String name, Optional<String> description) {
	public static AlbumResponse from(Album album) {
		return new AlbumResponse(album.getId(), album.getUserId(), album.getName(), album.getMaybeDescription());
	}
}
