package com.ruchij.photo.album.services.photo;

import com.ruchij.photo.album.components.id.IdGenerator;
import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.album.AlbumRepository;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.daos.photo.PhotoRepository;
import com.ruchij.photo.album.daos.resource.ResourceFile;
import com.ruchij.photo.album.services.exceptions.ResourceNotFoundException;
import com.ruchij.photo.album.services.models.FileData;
import com.ruchij.photo.album.services.storage.Storage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
public class PhotoServiceImpl implements PhotoService {
	private final Storage storage;
	private final AlbumRepository albumRepository;
	private final PhotoRepository photoRepository;
	private final IdGenerator idGenerator;
	private final Clock clock;

	public PhotoServiceImpl(
		Storage storage,
		AlbumRepository albumRepository,
		PhotoRepository photoRepository,
		IdGenerator idGenerator,
		Clock clock
	) {
		this.storage = storage;
		this.albumRepository = albumRepository;
		this.photoRepository = photoRepository;
		this.idGenerator = idGenerator;
		this.clock = clock;
	}

	@Override
	public Photo insert(String albumId, FileData fileData, Optional<String> maybeTitle, Optional<String> maybeDescription) throws IOException {
		Album album =
			albumRepository.findById(albumId)
				.orElseThrow(() -> new ResourceNotFoundException(albumId, Album.class));

		String photoId = idGenerator.generateId(Photo.class);
		Instant instant = clock.instant();

		ResourceFile resourceFile = storage.insert(fileData);

		Photo photo = new Photo();
		photo.setId(photoId);
		photo.setCreatedAt(instant);
		photo.setAlbum(album);
		photo.setMaybeTitle(maybeTitle);
		photo.setMaybeDescription(maybeDescription);
		photo.setResourceFile(resourceFile);

		Photo savedPhoto = photoRepository.save(photo);

		return savedPhoto;
	}

	@Override
	public Optional<Photo> findByPhotoId(String photoId) {
		return photoRepository.findById(photoId);
	}

	@Override
	public FileData getFileDataByPhotoId(String photoId) throws IOException {
		Photo photo =
			photoRepository.findById(photoId)
				.orElseThrow(() -> new ResourceNotFoundException(photoId, Photo.class));

		FileData fileData = storage.getFileDataByResourceId(photo.getResourceFile().getId());

		return fileData;
	}

	@Override
	public Photo deletePhotoById(String photoId) throws IOException {
		Photo photo =
			photoRepository.findById(photoId)
				.orElseThrow(() -> new ResourceNotFoundException(photoId, Photo.class));

		photoRepository.deleteById(photoId);
		storage.deleteByResourceFileId(photo.getResourceFile().getId());

		return photo;
	}
}
