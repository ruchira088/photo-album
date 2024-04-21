package com.ruchij.photo.album.services.album;

import com.ruchij.photo.album.components.id.IdGenerator;
import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.album.AlbumCover;
import com.ruchij.photo.album.daos.album.AlbumPassword;
import com.ruchij.photo.album.daos.album.AlbumRepository;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.daos.photo.PhotoRepository;
import com.ruchij.photo.album.daos.resource.ResourceFile;
import com.ruchij.photo.album.daos.user.User;
import com.ruchij.photo.album.services.exceptions.ResourceNotFoundException;
import com.ruchij.photo.album.services.models.Dimensions;
import com.ruchij.photo.album.services.models.FileData;
import com.ruchij.photo.album.services.models.ImageData;
import com.ruchij.photo.album.services.photo.PhotoService;
import com.ruchij.photo.album.services.storage.Storage;
import com.ruchij.photo.album.services.usage.UsageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumServiceImpl implements AlbumService {
	private static final Logger logger = LoggerFactory.getLogger(AlbumServiceImpl.class);

	private final AlbumRepository albumRepository;
	private final PhotoRepository photoRepository;
	private final UsageService usageService;
	private final Storage storage;
	private final PasswordEncoder passwordEncoder;
	private final IdGenerator idGenerator;
	private final Clock clock;

	public AlbumServiceImpl(
		AlbumRepository albumRepository,
		PhotoRepository photoRepository,
		UsageService usageService,
		Storage storage,
		PasswordEncoder passwordEncoder,
		IdGenerator idGenerator,
		Clock clock
	) {
		this.albumRepository = albumRepository;
		this.photoRepository = photoRepository;
		this.usageService = usageService;
		this.storage = storage;
		this.passwordEncoder = passwordEncoder;
		this.idGenerator = idGenerator;
		this.clock = clock;
	}

	@Override
	public Album create(String name, Optional<String> description, boolean isPublic, Optional<String> password, User user) {
		String albumId = idGenerator.generateId(Album.class);
		Instant instant = clock.instant();

		Album album = new Album();
		album.setId(albumId);
		album.setCreatedAt(instant);
		album.setName(name);
		album.setDescription(description);
		album.setUser(user);
		album.setPublic(isPublic);

		password.filter(__ -> !isPublic)
			.ifPresent(plainTextPassword -> {
				AlbumPassword albumPassword = new AlbumPassword();
				albumPassword.setHashedPassword(passwordEncoder.encode(plainTextPassword));
				albumPassword.setAlbum(album);

				album.setAlbumPassword(Optional.of(albumPassword));
			});

		Album saved = albumRepository.save(album);
		usageService.change(
			user.getId(),
			Optional.of(1),
			Optional.empty(),
			Optional.empty()
		);

		return saved;
	}

	@Override
	public List<Album> getAll(Optional<User> userOptional, int pageSize, int pageNumber) {
		Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
		return userOptional
			.map(user ->
				albumRepository.getAlbumsByUserIdOrIsPublicIsTrueOrAlbumPasswordEmpty(user.getId(), pageable)
			)
			.orElseGet(() -> albumRepository.getAlbumsByIsPublicIsTrueOrAlbumPasswordEmpty(pageable));
	}

	@Override
	public List<Album> getByUser(String userId, int pageSize, int pageNumber) {
		Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

		return albumRepository.getAlbumsByUserId(userId, pageable);
	}

	@Override
	@PreAuthorize("hasPermission(#albumId, 'ALBUM', 'READ')")
	public Album getByAlbumId(String albumId) {
		return albumRepository.findById(albumId)
			.orElseThrow(() -> new ResourceNotFoundException(albumId, Album.class));
	}

	@Override
	@PreAuthorize("hasPermission(#albumId, 'ALBUM', 'WRITE')")
	public Album deleteById(String albumId) {
		Album album = getByAlbumId(albumId);

		List<Photo> photos = photoRepository.findPhotosByAlbumId(albumId);

		photos.forEach(photo -> {
			photoRepository.deleteById(photo.getId());

			try {
				storage.deleteByResourceFileId(photo.getResourceFile().getId());
			} catch (IOException ioException) {
				logger.warn(
					"Unable to delete resource file id=%s".formatted(photo.getResourceFile().getId()),
					ioException
				);
			}
		});

		albumRepository.deleteById(albumId);
		usageService.change(
			album.getUser().getId(),
			Optional.of(-1),
			Optional.empty(),
			Optional.empty()
		);

		return album;
	}

	@Override
	@PreAuthorize("hasPermission(#albumId, 'ALBUM', 'WRITE')")
	public Album updateById(String albumId, String name, Optional<String> description, boolean isPublic) {
		Album album = getByAlbumId(albumId);

		album.setName(name);
		album.setDescription(description);
		album.setPublic(isPublic);

		Album savedAlbum = albumRepository.save(album);

		return savedAlbum;
	}

	@Override
	@PreAuthorize("hasPermission(#albumId, 'ALBUM', 'WRITE')")
	public Album setAlbumCover(String albumId, FileData fileData, Optional<Dimensions> dimensions) throws IOException {
		Album album = getByAlbumId(albumId);
		ImageData imageData = PhotoService.getImageDimensions(fileData, dimensions);

		ResourceFile resourceFile = storage.insert(imageData.fileData());

		usageService.change(album.getUser().getId(), Optional.empty(), Optional.of(1), Optional.of(fileData.size()));

		AlbumCover albumCover = new AlbumCover();
		albumCover.setAlbum(album);
		albumCover.setCreatedAt(clock.instant());
		albumCover.setHeight(imageData.dimensions().height());
		albumCover.setWidth(imageData.dimensions().width());
		albumCover.setResourceFile(resourceFile);

		album.setAlbumCover(Optional.of(albumCover));

		Album savedAlbum = albumRepository.save(album);

		return savedAlbum;
	}

	@Override
	@PreAuthorize("hasPermission(#albumId, 'ALBUM', 'READ')")
	public FileData getAlbumCover(String albumId) throws IOException {
		Album album = getByAlbumId(albumId);

		ResourceFile resourceFile =
			album.getAlbumCover().map(AlbumCover::getResourceFile)
				.orElseThrow(() ->
					new ResourceNotFoundException("Album cover NOT found for albumId=%s".formatted(albumId))
				);

		FileData fileData = storage.getFileDataByResourceId(resourceFile.getId());

		return fileData;
	}

	@Override
	@PreAuthorize("hasPermission(#albumId, 'ALBUM', 'READ')")
	public List<Photo> findPhotosByAlbumId(String albumId, int pageSize, int pageNumber) {
		albumRepository.findById(albumId).orElseThrow(() -> new ResourceNotFoundException(albumId, Album.class));

		return photoRepository.findPhotosByAlbumId(albumId, Pageable.ofSize(pageSize).withPage(pageNumber)).getContent();
	}
}
