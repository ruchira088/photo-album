package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.daos.usage.Usage;
import com.ruchij.photo.album.daos.usage.UsageRepository;
import com.ruchij.photo.album.daos.user.User;
import com.ruchij.photo.album.daos.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UsageRepository usageRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void shouldCreateUser() throws Exception {
		MockHttpServletRequestBuilder requestBuilder =
			MockMvcRequestBuilders
				.post("/user")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("""
					 {
						"email": "me@ruchij.com",
						"password": "Passw0rd",
						"firstName": "Ruchira",
						"lastName": "Jayasekara"
					}
					""");

		mockMvc.perform(requestBuilder)
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json("""
				  {
						"email": "me@ruchij.com",
						"firstName": "Ruchira",
						"lastName": "Jayasekara"
					}
				"""));

		User user = userRepository.findByEmail("me@ruchij.com")
			.orElseThrow(() -> new Exception("User not found"));

		assertEquals("me@ruchij.com", user.getEmail());
		assertEquals("Ruchira", user.getFirstName());
		assertEquals(Optional.of("Jayasekara"), user.getLastName());

		Usage usage = usageRepository.findById(user.getId()).orElseThrow();

		assertEquals(0, usage.getAlbumCount());
		assertEquals(0, usage.getPhotoCount());
		assertEquals(0, usage.getBytesUsed());

		assertTrue(passwordEncoder.matches("Passw0rd", user.getCredentials().getPassword()));
	}
}
