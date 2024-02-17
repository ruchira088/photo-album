package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.services.models.FileData;
import com.ruchij.photo.album.services.photo.PhotoService;
import com.ruchij.photo.album.web.responses.PhotoResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/photo")
public class PhotoController {
	private final PhotoService photoService;

	public PhotoController(PhotoService photoService) {
		this.photoService = photoService;
	}

	@ResponseBody
	@GetMapping(value = "id/{photoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PhotoResponse getPhotoById(@PathVariable String photoId) {
		Photo photo = photoService.findByPhotoId(photoId).orElseThrow();

		return PhotoResponse.from(photo);
	}

	@GetMapping("id/{photoId}/image-file")
	public ResponseEntity<InputStream> getImageFileByPhotoId(@PathVariable String photoId) throws IOException {
		FileData fileData = photoService.getFileDataByPhotoId(photoId);
		MediaType mediaType = MediaType.parseMediaType(fileData.contentType());

		return ResponseEntity.ok()
			.contentType(mediaType)
			.contentLength(fileData.size())
			.body(fileData.data());
	}
}
