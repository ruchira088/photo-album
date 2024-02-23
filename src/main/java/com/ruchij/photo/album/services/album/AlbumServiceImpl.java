package com.ruchij.photo.album.services.album;

import com.ruchij.photo.album.components.id.IdGenerator;
import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.album.AlbumPassword;
import com.ruchij.photo.album.daos.album.AlbumRepository;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.daos.photo.PhotoRepository;
import com.ruchij.photo.album.daos.user.User;
import com.ruchij.photo.album.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumServiceImpl implements AlbumService {
	private final AlbumRepository albumRepository;
	private final PhotoRepository photoRepository;
	private final PasswordEncoder passwordEncoder;
	private final IdGenerator idGenerator;
	private final Clock clock;

	public AlbumServiceImpl(
		AlbumRepository albumRepository,
		PhotoRepository photoRepository,
		PasswordEncoder passwordEncoder,
		IdGenerator idGenerator,
		Clock clock
	) {
		this.albumRepository = albumRepository;
		this.photoRepository = photoRepository;
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

				album.setAlbumPassword(albumPassword);
			});

		return albumRepository.save(album);
	}

	@Override
	public Optional<Album> findByAlbumId(String albumId) {
		return albumRepository.findById(albumId);
	}

	@Override
	public List<Photo> findPhotosByAlbumId(String albumId, int pageSize, int pageNumber) {
		albumRepository.findById(albumId).orElseThrow(() -> new ResourceNotFoundException(albumId, Album.class));

		return photoRepository.findPhotosByAlbumId(albumId, Pageable.ofSize(pageSize).withPage(pageNumber)).getContent();
	}
}
