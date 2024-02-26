package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.daos.user.User;
import com.ruchij.photo.album.services.album.AlbumService;
import com.ruchij.photo.album.services.exceptions.ResourceNotFoundException;
import com.ruchij.photo.album.services.models.FileData;
import com.ruchij.photo.album.services.photo.PhotoService;
import com.ruchij.photo.album.web.controllers.requests.CreateAlbumRequest;
import com.ruchij.photo.album.web.controllers.responses.AlbumResponse;
import com.ruchij.photo.album.web.controllers.responses.AlbumSummaryResponse;
import com.ruchij.photo.album.web.controllers.responses.PhotoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/album", produces = MediaType.APPLICATION_JSON_VALUE)
public class AlbumController {
	private final AlbumService albumService;
	private final PhotoService photoService;

	public AlbumController(AlbumService albumService, PhotoService photoService) {
		this.albumService = albumService;
		this.photoService = photoService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public AlbumResponse create(
		@RequestBody CreateAlbumRequest createAlbumRequest,
		@AuthenticationPrincipal User user
	) {
		Album album =
			albumService.create(
				createAlbumRequest.name(),
				createAlbumRequest.description(),
				createAlbumRequest.isPublic(),
				createAlbumRequest.password(),
				user
			);
		return AlbumResponse.from(album);
	}

	@GetMapping
	public List<AlbumSummaryResponse> getAll(
		@AuthenticationPrincipal User user,
		@RequestParam(value = "page-size", defaultValue = "40") Integer pageSize,
		@RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber
	) {
		return albumService.getAll(Optional.ofNullable(user), pageSize, pageNumber).stream()
			.map(AlbumSummaryResponse::from)
			.toList();
	}

	@GetMapping("/user")
	public List<AlbumResponse> getAllByUser(
		@AuthenticationPrincipal User user,
		@RequestParam(value = "page-size", defaultValue = "40") Integer pageSize,
		@RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber
	) {
		return albumService
			.getByUser(user.getId(),pageSize, pageNumber).stream().map(AlbumResponse::from)
			.toList();
	}

	@GetMapping(path = "/id/{albumId}")
	public AlbumResponse findById(@PathVariable String albumId) {
		return albumService.findByAlbumId(albumId).map(AlbumResponse::from)
			.orElseThrow(() -> new ResourceNotFoundException(albumId, Album.class));
	}

	@PostMapping(path = "/id/{albumId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public PhotoResponse insertPhoto(
		@PathVariable String albumId,
		@RequestParam(name = "photo") MultipartFile photoFile,
		@RequestParam Optional<String> title,
		@RequestParam Optional<String> description
	) throws IOException {
		FileData fileData = new FileData(photoFile.getOriginalFilename(), photoFile.getContentType(), photoFile.getSize(), photoFile.getInputStream());

		Photo photo = photoService.insert(albumId, fileData, title, description);

		return PhotoResponse.from(photo);
	}

	@GetMapping(value = "/id/{albumId}/photo")
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
