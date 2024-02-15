package com.ruchij.photo.album.services.album;

import com.ruchij.photo.album.dao.album.Album;

import java.util.Optional;

public interface AlbumService {
  Album create(String userId, String name, Optional<String> description);

  Optional<Album> findById(String albumId);
}
