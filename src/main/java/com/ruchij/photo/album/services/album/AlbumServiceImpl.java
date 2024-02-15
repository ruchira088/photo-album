package com.ruchij.photo.album.services.album;

import com.ruchij.photo.album.components.id.IdGenerator;
import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.album.AlbumRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
public class AlbumServiceImpl implements AlbumService {
  private final AlbumRepository albumRepository;
  private final IdGenerator idGenerator;
  private final Clock clock;

  public AlbumServiceImpl(AlbumRepository albumRepository, IdGenerator idGenerator, Clock clock) {
	this.albumRepository = albumRepository;
	this.idGenerator = idGenerator;
	this.clock = clock;
  }

  @Override
  public Album create(String userId, String name, Optional<String> maybeDescription) {
	String albumId = idGenerator.generateId(Album.class);
	Instant createdAt = clock.instant();

	Album album = new Album(albumId, createdAt, userId, name, maybeDescription);

	return albumRepository.save(album);
  }

  @Override
  public Optional<Album> findById(String albumId) {
	return albumRepository.findById(albumId);
  }
}
