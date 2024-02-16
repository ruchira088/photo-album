package com.ruchij.photo.album.services.storage;

import com.ruchij.photo.album.components.id.IdGenerator;
import com.ruchij.photo.album.daos.resource.ResourceFile;
import com.ruchij.photo.album.daos.resource.ResourceFileRepository;
import com.ruchij.photo.album.services.models.FileData;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

@Service
public class StorageImpl implements Storage {
	private final ResourceFileRepository resourceFileRepository;
	private final StorageBackend storageBackend;
	private final IdGenerator idGenerator;
	private final Clock clock;

	public StorageImpl(ResourceFileRepository resourceFileRepository, StorageBackend storageBackend, IdGenerator idGenerator, Clock clock) {
		this.resourceFileRepository = resourceFileRepository;
		this.storageBackend = storageBackend;
		this.idGenerator = idGenerator;
		this.clock = clock;
	}

	@Override
	public ResourceFile insert(FileData fileData) throws IOException {
		String resourceFileId = idGenerator.generateId(ResourceFile.class);
		Instant instant = clock.instant();

		String fileKey = idGenerator.generateId() + "-" + fileData.name();
		storageBackend.save(fileKey, fileData.data());

		ResourceFile resourceFile = new ResourceFile();
		resourceFile.setId(resourceFileId);
		resourceFile.setName(fileData.name());
		resourceFile.setCreatedAt(instant);
		resourceFile.setContentType(fileData.contentType());
		resourceFile.setFileKey(fileKey);
		resourceFile.setFileSize(fileData.size());

		ResourceFile savedResourceFile = resourceFileRepository.save(resourceFile);

		return savedResourceFile;
	}

	@Override
	public StorageBackend getStorageBackend() {
		return storageBackend;
	}
}
