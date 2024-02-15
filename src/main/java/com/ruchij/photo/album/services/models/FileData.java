package com.ruchij.photo.album.services.models;

import java.io.InputStream;

public record FileData(String name, String contentType, Long size, InputStream data) {
}