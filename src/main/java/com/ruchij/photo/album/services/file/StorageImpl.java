package com.ruchij.photo.album.services.file;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class StorageImpl implements Storage {
	@Override
	public String save(String key, InputStream inputStream) throws IOException {
		return UUID.randomUUID() + "-" + key;
	}

	@Override
	public InputStream get(String key) throws IOException {
		return null;
	}
}