package com.ruchij.photo.album.daos.album;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends CrudRepository<Album, String>, PagingAndSortingRepository<Album, String> {
	List<Album> getAlbumsByIsPublicIsTrueOrAlbumPasswordEmpty(Pageable pageable);

	List<Album> getAlbumsByUserIdOrIsPublicIsTrueOrAlbumPasswordEmpty(String userId, Pageable pageable);

	List<Album> getAlbumsByUserId(String userId, Pageable pageable);

	List<Album> getAlbumsByIdIsIn(List<String> albumIds);
}
