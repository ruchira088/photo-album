package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.services.album.AlbumService;
import com.ruchij.photo.album.services.models.FileData;
import com.ruchij.photo.album.services.photo.PhotoService;
import com.ruchij.photo.album.services.storage.StorageBackend;
import com.ruchij.photo.album.services.user.UserService;
import com.ruchij.photo.album.web.test.AuthenticationDetails;
import com.ruchij.photo.album.web.test.Helpers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class PhotoControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AlbumService albumService;

	@Autowired
	private PhotoService photoService;

	@Autowired
	private UserService userService;

	@Autowired
	private StorageBackend storageBackend;

	private AuthenticationDetails authenticationDetails;

	private Album album;

	private Photo photo;

	@BeforeAll
	public void beforeAll() throws Exception {
		authenticationDetails = Helpers.createAndAuthenticateUser(userService, mockMvc);

		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
			new UsernamePasswordAuthenticationToken(authenticationDetails.user(), null);
		SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

		album = albumService.create("My Photo Album", Optional.of("This is the description"), false, Optional.empty(), authenticationDetails.user());
		byte[] data = imageData();
		FileData fileData = new FileData("profile-image.jpeg", MediaType.IMAGE_JPEG_VALUE, (long) data.length, new ByteArrayInputStream(data));

		photo = photoService.insert(album.getId(), fileData, Optional.of("photo-1"), Optional.empty(), Optional.empty());
	}

	private byte[] imageData() throws IOException {
		return getClass().getClassLoader().getResourceAsStream("profile-image.jpeg").readAllBytes();
	}

	@Test
	public void shouldReturnPhotoById() throws Exception {
		MockHttpServletRequestBuilder requestBuilder =
			MockMvcRequestBuilders
				.get("/photo/id/%s".formatted(photo.getId()))
				.cookie(authenticationDetails.cookies());

		mockMvc.perform(requestBuilder)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json("""
								{"id":"%s","albumId":"%s","title":"photo-1"}
				""".formatted(photo.getId(), album.getId()))
			);
	}

	@Test
	public void shouldReturnNotFoundResponseForNonExistingPhotoId() throws Exception {
		MockHttpServletRequestBuilder requestBuilder =
			MockMvcRequestBuilders
				.get("/photo/id/non-existing-photo-id");

		mockMvc.perform(requestBuilder)
			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json("""
					{"errors":["Unable to find Photo with id=non-existing-photo-id"]}
				"""));
	}

	@Test
	public void shouldReturnAuthorizationErrorWhenRetrievingPhotoWithoutAuthentication() throws Exception {
		MockHttpServletRequestBuilder requestBuilder =
			MockMvcRequestBuilders
				.get("/photo/id/%s".formatted(photo.getId()));

		mockMvc.perform(requestBuilder)
			.andExpect(status().isForbidden())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json("""
				{"errors":["%s is a private photo album"]}
				""".formatted(photo.getAlbum().getId())));
	}

	@Test
	public void shouldServePhotoImageFile() throws Exception {
		byte[] bytes = imageData();

		MockHttpServletRequestBuilder requestBuilder =
			MockMvcRequestBuilders
				.get("/photo/id/%s/image-file".formatted(photo.getId()))
				.cookie(authenticationDetails.cookies());

		mockMvc.perform(requestBuilder)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
			.andExpect(content().bytes(bytes))
			.andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, bytes.length));
	}

	@AfterAll
	public void afterAll() throws IOException {
		storageBackend.delete(photo.getResourceFile().getFileKey());
	}

}