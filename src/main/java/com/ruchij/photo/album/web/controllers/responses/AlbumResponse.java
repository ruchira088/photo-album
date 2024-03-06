package com.ruchij.photo.album.web.controllers.responses;

import com.ruchij.photo.album.daos.album.Album;

import java.util.Optional;

public record AlbumResponse(
	String id,
	String userId,
	String name,
	Optional<String> description,
	boolean isPublic,
	boolean isPasswordProtected,
	Optional<AlbumCoverResponse> albumCover
) {
	public record AlbumCoverResponse(int width, int height) {}

	public static AlbumResponse from(Album album) {
		return new AlbumResponse(
			album.getId(),
			album.getUser().getId(),
			album.getName(),
			album.getDescription(),
			album.getPublic(),
			album.getAlbumPassword().isPresent(),
			album.getAlbumCover().map(albumCover -> new AlbumCoverResponse(albumCover.getWidth(), albumCover.getHeight()))
		);
	}
}
