package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.services.exceptions.ResourceNotFoundException;
import com.ruchij.photo.album.services.models.FileData;
import com.ruchij.photo.album.services.photo.PhotoService;
import com.ruchij.photo.album.web.responses.PhotoResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping("/photo")
public class PhotoController {
	private final PhotoService photoService;

	public PhotoController(PhotoService photoService) {
		this.photoService = photoService;
	}

	@ResponseBody
	@GetMapping(value = "/id/{photoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PhotoResponse getPhotoById(@PathVariable String photoId) {
		Photo photo =
			photoService.findByPhotoId(photoId)
				.orElseThrow(() -> new ResourceNotFoundException(photoId, Photo.class));

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
