package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.services.album.AlbumService;
import com.ruchij.photo.album.services.models.FileData;
import com.ruchij.photo.album.services.photo.PhotoService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

	private Album album;

	private Photo photo;

	@BeforeAll
	public void beforeAll() throws IOException {
		album = albumService.create("My Photo Album", Optional.of("This is the description"));
		byte[] data = getClass().getClassLoader().getResourceAsStream("maltese-dog.jpg").readAllBytes();
		FileData fileData = new FileData("maltese-dog.jpg", "image/jpg", (long) data.length, new ByteArrayInputStream(data));

		photo = photoService.insert(album.getId(), fileData, Optional.of("photo-1"), Optional.empty());
	}

	@Test
	public void shouldReturnPhotoById() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/photo/id/%s".formatted(photo.getId()));

		mockMvc.perform(requestBuilder)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json("""
    				{"id":"%s","albumId":"%s","title":"photo-1"}
				""".formatted(photo.getId(), album.getId()))
			);
	}

	@AfterAll
	public void afterAll() throws IOException {
		photoService.deletePhotoById(photo.getId());
	}

}