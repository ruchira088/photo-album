package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.components.id.IdGenerator;
import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.album.AlbumRepository;
import com.ruchij.photo.album.services.file.Storage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AlbumControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AlbumRepository albumRepository;

	@SpyBean
	private IdGenerator idGenerator;

	@Autowired
	private Storage storage;

	@Test
	void shouldCreateAndRetrieveAlbumInDatabase() throws Exception {
		Mockito.doReturn("mock-album-id")
			.when(idGenerator).generateId(Mockito.eq(Album.class));

		String expectedJson = """
						{"name": "album-name","description": "album-description"}
			""";

		MockHttpServletRequestBuilder createAlbumRequestBuilder =
			MockMvcRequestBuilders
				.post("/album")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("""
						{"name": "album-name","description": "album-description"}
					"""
				);

		mockMvc.perform(createAlbumRequestBuilder)
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(expectedJson));

		Optional<Album> maybeSavedAlbum = albumRepository.findById("mock-album-id");
		Assertions.assertTrue(maybeSavedAlbum.isPresent());

		Album savedAlbum = maybeSavedAlbum.get();
		assertEquals("mock-album-id", savedAlbum.getId());
		assertEquals("album-name", savedAlbum.getName());
		assertEquals(Optional.of("album-description"), savedAlbum.getMaybeDescription());

		MockHttpServletRequestBuilder getAlbumRequestBuilder =
			MockMvcRequestBuilders.get("/album/id/mock-album-id");

		mockMvc.perform(getAlbumRequestBuilder)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(expectedJson));
	}

	@Test
	void shouldSavePhoto() throws Exception {
		Album album = new Album("my-album-id", Instant.now(), "album-name", Optional.of("album-description"));
		albumRepository.save(album);

		Mockito.doReturn("id-1", "id-2", "id-3").when(idGenerator).generateId();

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("maltese-dog.jpg");

		MockMultipartFile multipartFile =
			new MockMultipartFile(
				"photo",
				"maltese-dog.jpg",
				"image/jpg",
				inputStream
			);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
			.multipart("/album/id/my-album-id/photo")
			.file(multipartFile)
			.param("title", "photo-title")
			.param("description", "photo-description");

		mockMvc.perform(requestBuilder)
			.andExpect(status().isOk());



	}
}