package com.ruchij.photo.album.services.album;

import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.photo.Photo;

import java.util.List;
import java.util.Optional;

public interface AlbumService {
	Album create(String name, Optional<String> description);

	Optional<Album> findByAlbumId(String albumId);

	List<Photo> findPhotosByAlbumId(String albumId, int pageSize, int pageNumber);
}
