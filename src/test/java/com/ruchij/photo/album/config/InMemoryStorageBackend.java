package com.ruchij.photo.album.config;

import com.ruchij.photo.album.services.storage.StorageBackend;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorageBackend implements StorageBackend {
	private final Map<String, byte[]> fileSystem = new ConcurrentHashMap<>();

	@Override
	public void save(String key, InputStream inputStream) throws IOException {
		fileSystem.put(key, inputStream.readAllBytes());
	}

	@Override
	public InputStream get(String key) throws IOException {
		byte[] data = fileSystem.get(key);

		if (data == null) {
			throw new FileNotFoundException("Unable to find file: " + key);
		}

		return new ByteArrayInputStream(data);
	}

	@Override
	public boolean delete(String key) throws IOException {
		boolean result = fileSystem.remove(key) != null;
		return result;
	}
}
