package com.ruchij.photo.album.services.usage;

import com.ruchij.photo.album.daos.usage.Usage;
import com.ruchij.photo.album.daos.usage.UsageRepository;
import com.ruchij.photo.album.services.models.UsageInformation;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
public class UsageServiceImpl implements UsageService {
	private final UsageRepository usageRepository;
	private final Clock clock;

	public UsageServiceImpl(UsageRepository usageRepository, Clock clock) {
		this.usageRepository = usageRepository;
		this.clock = clock;
	}

	@Override
	public UsageInformation create(String userId) {
		Usage usage = new Usage();
		usage.setUserId(userId);

		Instant instant = clock.instant();
		usage.setLastUpdatedAt(instant);

		usage.setAlbumCount(0);
		usage.setPhotoCount(0);
		usage.setBytesUsed(0);

		Usage saved = usageRepository.save(usage);

		return UsageInformation.from(saved);
	}

	@Override
	public UsageInformation getUsage(String userId) {
		Usage usage = usageRepository.findById(userId).orElseThrow();

		return UsageInformation.from(usage);
	}

	@Override
	public UsageInformation change(
		String userId,
		Optional<Integer> albumCountDiff,
		Optional<Integer> photoCountDiff,
		Optional<Long> dataUsedDiff
	) {
		Instant instant = clock.instant();

		usageRepository.update(
			userId,
			instant,
			albumCountDiff.orElse(0),
			photoCountDiff.orElse(0),
			dataUsedDiff.orElse(0L)
		);

		return getUsage(userId);
	}
}
