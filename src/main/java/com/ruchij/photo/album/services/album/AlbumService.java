package com.ruchij.photo.album.services.album;

import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.album.AlbumCover;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.daos.user.User;
import com.ruchij.photo.album.services.models.Dimensions;
import com.ruchij.photo.album.services.models.FileData;
import com.ruchij.photo.album.services.models.ImageData;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AlbumService {
	Album create(String name, Optional<String> description, boolean isPublic, Optional<String> password, User user);

	List<Album> getAll(Optional<User> user, int pageSize, int pageNumber);

	List<Album> getByUser(String userId, int pageSize, int pageNumber);

	Album getByAlbumId(String albumId);

	Album deleteById(String albumId);

	Album updateById(String albumId, String name, Optional<String> description, boolean isPublic);

	Album setAlbumCover(String albumId, FileData fileData, Optional<Dimensions> dimensions) throws IOException;

	FileData getAlbumCover(String albumId) throws IOException;

	List<Photo> findPhotosByAlbumId(String albumId, int pageSize, int pageNumber);
}
