package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.components.id.IdGenerator;
import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.album.AlbumRepository;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.daos.photo.PhotoRepository;
import com.ruchij.photo.album.daos.resource.ResourceFile;
import com.ruchij.photo.album.daos.resource.ResourceFileRepository;
import com.ruchij.photo.album.services.storage.StorageBackend;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

	@Autowired
	private ResourceFileRepository resourceFileRepository;

	@Autowired
	private PhotoRepository photoRepository;

	@SpyBean
	private IdGenerator idGenerator;

	@Autowired
	private StorageBackend storageBackend;

	@Value("${application.authentication.secret}")
	private String authenticationSecret;

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
				.header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(authenticationSecret))
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
			MockMvcRequestBuilders
				.get("/album/id/mock-album-id")
				.header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(authenticationSecret));

		mockMvc.perform(getAlbumRequestBuilder)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(expectedJson));
	}

	@Test
	void shouldSavePhotoInAlbum() throws Exception {
		String fileKey = "id-3-maltese-dog.jpg";
		try {
			Album album = new Album("my-album-id", Instant.now(), "album-name", Optional.of("album-description"));
			albumRepository.save(album);

			Mockito.doReturn("id-1", "id-2", "id-3").when(idGenerator).generateId();

			byte[] data = getClass().getClassLoader().getResourceAsStream("maltese-dog.jpg").readAllBytes();

			MockMultipartFile multipartFile =
				new MockMultipartFile(
					"photo",
					"maltese-dog.jpg",
					MediaType.IMAGE_JPEG_VALUE,
					new ByteArrayInputStream(data)
				);

			MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.multipart("/album/id/my-album-id/photo")
				.file(multipartFile)
				.param("title", "photo-title")
				.param("description", "photo-description")
				.header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(authenticationSecret));

			mockMvc.perform(requestBuilder)
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().json("""
					{"id":"photo-id-1","albumId":"my-album-id","title":"photo-title","description":"photo-description"}
					"""));

			InputStream savedInputStream = storageBackend.get(fileKey);
			Assertions.assertArrayEquals(data, savedInputStream.readAllBytes());
		} finally {
			storageBackend.delete(fileKey);
		}
	}

	@Test
	public void shouldReturnPhotosOfTheAlbum() throws Exception {
		Album album = new Album("my-id", Instant.now(), "SampleTitle", Optional.empty());
		albumRepository.save(album);

		int count = 10;
		List<ResourceFile> resourceFiles = new ArrayList<>();

		for (int i = 1; i <= count; i++) {
			ResourceFile resourceFile = new ResourceFile();
			resourceFile.setId("resource-file-id-" + i);
			resourceFile.setName("file-" + i);
			resourceFile.setCreatedAt(Instant.now());
			resourceFile.setFileSize(i * 100L);
			resourceFile.setContentType(MediaType.IMAGE_JPEG_VALUE);
			resourceFile.setFileKey("file-key-" + i);

			resourceFileRepository.save(resourceFile);
			resourceFiles.add(resourceFile);
		}

		for (int i = 1; i <= count; i++) {
			Photo photo = new Photo();
			photo.setId("photo-" + i);
			photo.setCreatedAt(Instant.now());
			photo.setAlbum(album);
			photo.setResourceFile(resourceFiles.get(i - 1));

			photoRepository.save(photo);
		}

		MockHttpServletRequestBuilder requestBuilder =
			MockMvcRequestBuilders
				.get("/album/id/my-id/photo?page-size=3&page-number=1")
				.header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(authenticationSecret));

		mockMvc.perform(requestBuilder)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json("""
								[
									{"id":"photo-4","albumId":"my-id","title":null,"description":null},
						{"id":"photo-5","albumId":"my-id","title":null,"description":null},
						{"id":"photo-6","albumId":"my-id","title":null,"description":null}
					]
				""")
			);

	}
}