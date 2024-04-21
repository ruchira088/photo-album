package com.ruchij.photo.album.daos.usage;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface UsageRepository extends CrudRepository<Usage, String> {

	@Modifying
	@Query("UPDATE usage SET lastUpdatedAt = :lastUpdatedAt, albumCount = albumCount + :albumDiff, photoCount = photoCount + :photoDiff, bytesUsed = bytesUsed + :bytesUsedDiff WHERE userId = :userId")
	void update(
		@Param("userId") String userId,
		@Param("lastUpdatedAt") Instant lastUpdatedAt,
		@Param("albumDiff") int albumDiff,
		@Param("photoDiff") int photoDiff,
		@Param("bytesUsedDiff") long bytesUsedDiff
	);
}