package com.ruchij.photo.album.services.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class LocalFileStorageBackend implements StorageBackend {
	private final Path basePath;

	public LocalFileStorageBackend(@Value("${storage-backend.local-storage.base-path}") Path basePath) {
		this.basePath = basePath;
	}

	@Override
	public void save(String key, InputStream inputStream) throws IOException {
		Files.copy(inputStream, fullPath(key));
	}

	@Override
	public InputStream get(String key) throws IOException {
		return new BufferedInputStream(new FileInputStream(fullPath(key).toFile()));
	}

	@Override
	public boolean delete(String key) throws IOException {
		return Files.deleteIfExists(fullPath(key));
	}

	private Path fullPath(String key) {
		return basePath.resolve(key);
	}


}