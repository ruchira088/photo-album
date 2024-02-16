package com.ruchij.photo.album.daos.photo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends CrudRepository<Photo, String>, PagingAndSortingRepository<Photo, String> {
	Page<Photo> findPhotosByAlbumId(String albumId, Pageable pageable);
}
