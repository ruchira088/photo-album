package com.ruchij.photo.album.services.photo;

import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.services.models.FileData;

import java.io.IOException;
import java.util.Optional;

public interface PhotoService {
	Photo insert(
		String albumId,
		FileData fileData,
		Optional<String> maybeTitle,
		Optional<String> maybeDescription
	) throws IOException;

	Optional<Photo> findByPhotoId(String photoId);

	FileData getFileDataByPhotoId(String photoId) throws IOException;

	Photo deletePhotoById(String photoId) throws IOException;
}
