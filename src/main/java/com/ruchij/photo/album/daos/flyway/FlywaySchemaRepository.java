package com.ruchij.photo.album.daos.flyway;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlywaySchemaRepository extends PagingAndSortingRepository<FlywaySchema, String> {
}
