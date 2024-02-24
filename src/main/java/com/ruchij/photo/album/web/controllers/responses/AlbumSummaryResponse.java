package com.ruchij.photo.album.web.controllers.responses;

import com.ruchij.photo.album.daos.album.Album;

public record AlbumSummaryResponse(String albumId, boolean isPublic, boolean isPasswordProtected) {
	public static AlbumSummaryResponse from(Album album) {
		return new AlbumSummaryResponse(
			album.getId(),
			album.getPublic(),
			album.getAlbumPassword().isPresent()
		);
	}
}
