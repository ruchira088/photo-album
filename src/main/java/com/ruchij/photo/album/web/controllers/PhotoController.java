package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.services.exceptions.ResourceNotFoundException;
import com.ruchij.photo.album.services.models.FileData;
import com.ruchij.photo.album.services.photo.PhotoService;
import com.ruchij.photo.album.web.controllers.responses.PhotoResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/photo")
public class PhotoController {
	private final PhotoService photoService;

	public PhotoController(PhotoService photoService) {
		this.photoService = photoService;
	}

	@GetMapping(value = "/id/{photoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PhotoResponse getPhotoById(@PathVariable String photoId) {
		Photo photo = photoService.getByPhotoId(photoId);

		return PhotoResponse.from(photo);
	}

	@DeleteMapping(value = "/id/{photoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PhotoResponse deletePhotoById(@PathVariable String photoId) throws IOException {
		Photo photo = photoService.deletePhotoById(photoId);

		return PhotoResponse.from(photo);
	}

	@GetMapping("/id/{photoId}/image-file")
	public void getImageFileByPhotoId(@PathVariable String photoId, HttpServletResponse httpServletResponse) throws IOException {
		FileData fileData = photoService.getFileDataByPhotoId(photoId);

		httpServletResponse.setContentType(fileData.contentType());
		httpServletResponse.setStatus(HttpStatus.OK.value());
		httpServletResponse.addHeader(HttpHeaders.CONTENT_LENGTH, fileData.size().toString());
		fileData.data().transferTo(httpServletResponse.getOutputStream());
	}
}
