package com.ruchij.photo.album.services.file;

import com.ruchij.photo.album.daos.resource.ResourceFile;
import com.ruchij.photo.album.services.models.FileData;

import java.io.IOException;

public interface FileService {
	ResourceFile insert(FileData fileData) throws IOException;
}
