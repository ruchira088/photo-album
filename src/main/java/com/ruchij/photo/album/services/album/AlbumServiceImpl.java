package com.ruchij.photo.album.services.album;

import com.ruchij.photo.album.dao.album.Album;
import com.ruchij.photo.album.dao.album.AlbumRepository;
import com.ruchij.photo.album.components.id.IdGenerator;
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
  public Album create(String userId, String name, Optional<String> description) {
	String albumId = idGenerator.generateId(Album.class);
	Instant createdAt = clock.instant();

	Album album = new Album(albumId, createdAt, userId, name, description);

	return albumRepository.save(album);
  }
}
