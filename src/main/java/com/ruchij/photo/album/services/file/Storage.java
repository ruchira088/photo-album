package com.ruchij.photo.album.services.file;

import java.io.IOException;
import java.io.InputStream;

public interface Storage {

	String save(String key, InputStream inputStream) throws IOException;

	InputStream get(String key) throws IOException;
}
