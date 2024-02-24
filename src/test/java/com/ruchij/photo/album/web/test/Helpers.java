package com.ruchij.photo.album.web.test;

import com.github.javafaker.Faker;
import com.ruchij.photo.album.daos.user.User;
import com.ruchij.photo.album.services.user.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class Helpers {
	private static Faker faker = Faker.instance();
	private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public static AuthenticationDetails createAndAuthenticateUser(UserService userService, MockMvc mockMvc)
		throws Exception {
		String password = faker.internet().password();

		User user = userService.create(
			faker.internet().emailAddress(),
			password,
			faker.name().firstName(),
			Optional.of(faker.name().lastName())
		);

		MockHttpServletRequestBuilder loginRequest = MockMvcRequestBuilders
			.post("/auth/login")
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content("""
				{
					"email": "%s",
					"password": "%s"
				}
				""".formatted(user.getEmail(), password)
			);

		MvcResult result = mockMvc.perform(loginRequest)
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json("""
					{
						"userId": "%s",
						"email": "%s",
						"firstName": "%s",
						"lastName": "%s"
					}
				""".formatted(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName().get()))
			)
			.andReturn();

		return new AuthenticationDetails(user, result.getResponse().getCookies());
	}

}
