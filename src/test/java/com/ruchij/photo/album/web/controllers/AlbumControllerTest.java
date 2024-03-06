package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.components.id.IdGenerator;
import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.daos.photo.PhotoRepository;
import com.ruchij.photo.album.daos.resource.ResourceFile;
import com.ruchij.photo.album.daos.resource.ResourceFileRepository;
import com.ruchij.photo.album.services.album.AlbumService;
import com.ruchij.photo.album.services.storage.StorageBackend;
import com.ruchij.photo.album.services.user.UserService;
import com.ruchij.photo.album.web.test.AuthenticationDetails;
import com.ruchij.photo.album.web.test.Helpers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlbumControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AlbumService albumService;

	@Autowired
	private ResourceFileRepository resourceFileRepository;

	@Autowired
	private PhotoRepository photoRepository;

	@SpyBean
	private IdGenerator idGenerator;

	@Autowired
	private StorageBackend storageBackend;

	@Autowired
	private UserService userService;

	private AuthenticationDetails authenticationDetails;

	@BeforeAll
	void beforeAll() throws Exception {
		authenticationDetails = Helpers.createAndAuthenticateUser(userService, mockMvc);
	}

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
				.cookie(authenticationDetails.cookies())
				.content("""
						{"name": "album-name","description": "album-description", "isPublic": false, "password": "top-secret"}
					"""
				);

		mockMvc.perform(createAlbumRequestBuilder)
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(expectedJson));

		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
			new UsernamePasswordAuthenticationToken(authenticationDetails.user(), null);
		SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

		Album savedAlbum = albumService.getByAlbumId("mock-album-id");
		assertEquals("mock-album-id", savedAlbum.getId());
		assertEquals("album-name", savedAlbum.getName());
		assertEquals(Optional.of("album-description"), savedAlbum.getDescription());

		MockHttpServletRequestBuilder getAlbumRequestBuilder =
			MockMvcRequestBuilders
				.get("/album/id/mock-album-id")
				.cookie(authenticationDetails.cookies());

		mockMvc.perform(getAlbumRequestBuilder)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json(expectedJson));
	}

	@Test
	void shouldSavePhotoInAlbum() throws Exception {
		String fileKey = "id-3-profile-image.jpeg";
		try {
			Mockito.doReturn("id-0", "id-1", "id-2", "id-3").when(idGenerator).generateId();

			Album album =
				albumService.create(
					"album-name",
					Optional.of("album-description"),
					false,
					Optional.of("password"),
					authenticationDetails.user()
				);

			byte[] data = getClass().getClassLoader().getResourceAsStream("profile-image.jpeg").readAllBytes();

			MockMultipartFile multipartFile =
				new MockMultipartFile(
					"photo",
					"profile-image.jpeg",
					MediaType.IMAGE_JPEG_VALUE,
					new ByteArrayInputStream(data)
				);

			MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.multipart("/album/id/%s/photo".formatted(album.getId()))
				.file(multipartFile)
				.param("title", "photo-title")
				.param("description", "photo-description")
				.cookie(authenticationDetails.cookies());

			mockMvc.perform(requestBuilder)
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(content().json("""
					{"id":"photo-id-1","albumId":"album-id-0","title":"photo-title","description":"photo-description"}
					"""));

			InputStream savedInputStream = storageBackend.get(fileKey);
			Assertions.assertArrayEquals(data, savedInputStream.readAllBytes());
		} finally {
			storageBackend.delete(fileKey);
		}
	}

	@Test
	public void shouldReturnPhotosOfTheAlbum() throws Exception {
		Album album =
			albumService.create(
				"another-album-name",
				Optional.of("another-album-description"),
				false,
				Optional.of("password"),
				authenticationDetails.user()
			);

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
			photo.setWidth(1920);
			photo.setHeight(1080);

			photoRepository.save(photo);
		}

		MockHttpServletRequestBuilder requestBuilder =
			MockMvcRequestBuilders
				.get("/album/id/%s/photo?page-size=3&page-number=1".formatted(album.getId()))
				.cookie(authenticationDetails.cookies());

		mockMvc.perform(requestBuilder)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json("""
					[
						{"id":"photo-4","albumId":"%s","title":null,"description":null},
						{"id":"photo-5","albumId":"%s","title":null,"description":null},
						{"id":"photo-6","albumId":"%s","title":null,"description":null}
					]
				""".formatted(album.getId(), album.getId(), album.getId()))
			);

	}
}