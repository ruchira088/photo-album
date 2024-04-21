package com.ruchij.photo.album.services.photo;

import com.ruchij.photo.album.components.id.IdGenerator;
import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.album.AlbumRepository;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.daos.photo.PhotoRepository;
import com.ruchij.photo.album.daos.resource.ResourceFile;
import com.ruchij.photo.album.services.exceptions.ResourceNotFoundException;
import com.ruchij.photo.album.services.models.Dimensions;
import com.ruchij.photo.album.services.models.FileData;
import com.ruchij.photo.album.services.models.ImageData;
import com.ruchij.photo.album.services.storage.Storage;
import com.ruchij.photo.album.services.usage.UsageService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
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
	private final UsageService usageService;
	private final IdGenerator idGenerator;
	private final Clock clock;

	public PhotoServiceImpl(
		Storage storage,
		AlbumRepository albumRepository,
		PhotoRepository photoRepository,
		UsageService usageService,
		IdGenerator idGenerator,
		Clock clock
	) {
		this.storage = storage;
		this.albumRepository = albumRepository;
		this.photoRepository = photoRepository;
		this.usageService = usageService;
		this.idGenerator = idGenerator;
		this.clock = clock;
	}

	@Override
	@Transactional
	@PreAuthorize("hasPermission(#albumId, 'ALBUM', 'WRITE')")
	public Photo insert(String albumId, FileData fileData, Optional<String> title, Optional<String> description, Optional<Dimensions> dimensions) throws IOException {
		Album album =
			albumRepository.findById(albumId)
				.orElseThrow(() -> new ResourceNotFoundException(albumId, Album.class));

		String photoId = idGenerator.generateId(Photo.class);
		Instant instant = clock.instant();

		ImageData imageData = PhotoService.getImageDimensions(fileData, dimensions);
		ResourceFile resourceFile = storage.insert(imageData.fileData());

		Photo photo = new Photo();
		photo.setId(photoId);
		photo.setCreatedAt(instant);
		photo.setAlbum(album);
		photo.setTitle(title);
		photo.setWidth(imageData.dimensions().width());
		photo.setHeight(imageData.dimensions().height());
		photo.setDescription(description);
		photo.setResourceFile(resourceFile);

		Photo savedPhoto = photoRepository.save(photo);

		usageService.change(
			album.getUser().getId(),
			Optional.empty(),
			Optional.of(1),
			Optional.of(fileData.size())
		);

		return savedPhoto;
	}

	@Override
	@PreAuthorize("hasPermission(#photoId, 'PHOTO', 'READ')")
	public Photo getByPhotoId(String photoId) {
		Photo photo =
			photoRepository.findById(photoId)
				.orElseThrow(() -> new ResourceNotFoundException(photoId, Photo.class));

		return photo;
	}

	@Override
	@PreAuthorize("hasPermission(#photoId, 'PHOTO', 'READ')")
	public FileData getFileDataByPhotoId(String photoId) throws IOException {
		Photo photo =
			photoRepository.findById(photoId)
				.orElseThrow(() -> new ResourceNotFoundException(photoId, Photo.class));

		FileData fileData = storage.getFileDataByResourceId(photo.getResourceFile().getId());

		return fileData;
	}

	@Override
	@Transactional
	@PreAuthorize("hasPermission(#photoId, 'PHOTO', 'WRITE')")
	public Photo deletePhotoById(String photoId) throws IOException {
		Photo photo = getByPhotoId(photoId);
		photoRepository.deleteById(photoId);
		storage.deleteByResourceFileId(photo.getResourceFile().getId());

		String userId = photo.getAlbum().getUser().getId();
		usageService.change(
			userId,
			Optional.empty(),
			Optional.of(-1),
			Optional.of(-photo.getResourceFile().getFileSize())
		);

		return photo;
	}
}
