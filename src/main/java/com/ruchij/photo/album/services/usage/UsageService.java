package com.ruchij.photo.album.services.usage;

import com.ruchij.photo.album.services.models.UsageInformation;

import java.util.Optional;

public interface UsageService {
	UsageInformation create(String userId);

	UsageInformation getUsage(String userId);

	UsageInformation change(
		String userId,
		Optional<Integer> albumCountDiff,
		Optional<Integer> photoCountDiff,
		Optional<Long> dataUsedDiff
	);
}
