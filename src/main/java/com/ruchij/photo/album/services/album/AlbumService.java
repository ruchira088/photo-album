package com.ruchij.photo.album.services.album;

import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.daos.user.User;

import java.util.List;
import java.util.Optional;

public interface AlbumService {
	Album create(String name, Optional<String> description, boolean isPublic, Optional<String> password, User user);

	List<Album> getAll(Optional<User> user, int pageSize, int pageNumber);

	List<Album> getByUser(String userId, int pageSize, int pageNumber);

	Optional<Album> findByAlbumId(String albumId);

	List<Photo> findPhotosByAlbumId(String albumId, int pageSize, int pageNumber);
}
