package com.ruchij.photo.album.services.storage;

import com.ruchij.photo.album.daos.resource.ResourceFile;
import com.ruchij.photo.album.services.models.FileData;

import java.io.IOException;

public interface Storage {
	ResourceFile insert(FileData fileData) throws IOException;

	ResourceFile deleteByResourceFileId(String resourceFileId) throws IOException;

	FileData getFileDataByResourceId(String resourceFileId) throws IOException;

}
