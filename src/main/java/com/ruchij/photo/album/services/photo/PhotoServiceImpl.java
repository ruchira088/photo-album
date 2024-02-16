package com.ruchij.photo.album.services.photo;

import com.ruchij.photo.album.components.id.IdGenerator;
import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.daos.photo.PhotoRepository;
import com.ruchij.photo.album.daos.resource.ResourceFile;
import com.ruchij.photo.album.services.album.AlbumService;
import com.ruchij.photo.album.services.file.FileService;
import com.ruchij.photo.album.services.models.FileData;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
public class PhotoServiceImpl implements PhotoService {
	private final FileService fileService;
	private final AlbumService albumService;
	private final PhotoRepository photoRepository;
	private final IdGenerator idGenerator;
	private final Clock clock;

	public PhotoServiceImpl(
		FileService fileService,
		AlbumService albumService,
		PhotoRepository photoRepository,
		IdGenerator idGenerator,
		Clock clock
	) {
		this.fileService = fileService;
		this.albumService = albumService;
		this.photoRepository = photoRepository;
		this.idGenerator = idGenerator;
		this.clock = clock;
	}

	@Override
	public Photo insert(String albumId, FileData fileData, Optional<String> maybeTitle, Optional<String> maybeDescription) throws IOException {
		Album album = albumService.findById(albumId).orElseThrow();

		String photoId = idGenerator.generateId(Photo.class);
		Instant instant = clock.instant();

		ResourceFile resourceFile = fileService.insert(fileData);

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
}
