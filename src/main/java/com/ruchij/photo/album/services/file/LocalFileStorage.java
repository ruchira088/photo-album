package com.ruchij.photo.album.services.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileStorage implements Storage {
	private final Path basePath;

	public LocalFileStorage(@Value("${storage.local-storage.base-path}") Path basePath) {
		this.basePath = basePath;
	}

	@Override
	public String save(String key, InputStream inputStream) throws IOException {
		Path path = basePath.resolve(key);
		Files.copy(inputStream, path);

		return path.toAbsolutePath().toString();
	}

	@Override
	public InputStream get(String key) throws IOException {
		Path path = basePath.resolve(key);

		return new BufferedInputStream(new FileInputStream(path.toFile()));
	}
}