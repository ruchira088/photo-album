package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.services.album.AlbumService;
import com.ruchij.photo.album.services.models.FileData;
import com.ruchij.photo.album.services.photo.PhotoService;
import com.ruchij.photo.album.web.requests.CreateAlbumRequest;
import com.ruchij.photo.album.web.responses.AlbumResponse;
import com.ruchij.photo.album.web.responses.PhotoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/album")
public class AlbumController {
	private final AlbumService albumService;
	private final PhotoService photoService;

	public AlbumController(AlbumService albumService, PhotoService photoService) {
		this.albumService = albumService;
		this.photoService = photoService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AlbumResponse> create(@RequestBody CreateAlbumRequest createAlbumRequest) {
		Album album = albumService.create(createAlbumRequest.name(), createAlbumRequest.description());
		return ResponseEntity.status(HttpStatus.CREATED).body(AlbumResponse.from(album));
	}

	@ResponseBody
	@GetMapping(path = "id/{albumId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public AlbumResponse findById(@PathVariable String albumId) {
		return albumService.findByAlbumId(albumId).map(AlbumResponse::from).orElseThrow();
	}

	@PostMapping(path = "id/{albumId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PhotoResponse> insertPhoto(
		@PathVariable String albumId,
		@RequestParam(name = "photo") MultipartFile photoFile,
		@RequestParam Optional<String> title,
		@RequestParam Optional<String> description
	) throws IOException {
		FileData fileData = new FileData(photoFile.getOriginalFilename(), photoFile.getContentType(), photoFile.getSize(), photoFile.getInputStream());

		Photo photo = photoService.insert(albumId, fileData, title, description);

		return ResponseEntity.status(HttpStatus.CREATED).body(PhotoResponse.from(photo));
	}

	@ResponseBody
	@GetMapping(value = "id/{albumId}/photo", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PhotoResponse> getPhotos(
		@PathVariable String albumId,
		@RequestParam(value = "page-size", defaultValue = "40") Integer pageSize,
		@RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber
	) {
		List<PhotoResponse> photoResponses =
			albumService.findPhotosByAlbumId(albumId, pageSize, pageNumber).stream()
				.map(PhotoResponse::from).toList();

		return photoResponses;
	}

}
