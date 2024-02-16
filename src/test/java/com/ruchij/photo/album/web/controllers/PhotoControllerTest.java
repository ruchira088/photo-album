package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.album.AlbumRepository;
import com.ruchij.photo.album.daos.resource.ResourceFile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
class PhotoControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AlbumRepository albumRepository;

	@Test
	public void shouldReturnPhotoById() {
		Album album = new Album("another-album-id", Instant.now(), "My Photo Album", Optional.empty());
		albumRepository.save(album);

	}



}