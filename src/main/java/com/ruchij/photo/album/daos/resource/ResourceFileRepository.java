package com.ruchij.photo.album.daos.resource;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceFileRepository extends CrudRepository<ResourceFile, String> {
}
