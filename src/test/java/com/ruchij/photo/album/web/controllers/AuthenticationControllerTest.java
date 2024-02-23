package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.daos.user.User;
import com.ruchij.photo.album.services.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class AuthenticationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserService userService;

	@Test
	void shouldLoginUser() throws Exception {
		User user = userService.create("test@gmail.com", "my-password", "John", Optional.empty());

		MockHttpServletRequestBuilder loginRequest =
			MockMvcRequestBuilders.post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("""
					{ 
						"email": "test@gmail.com",
						"password": "my-password" 
					}
					""");

		MvcResult mvcResult = mockMvc.perform(loginRequest)
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json("""
					{
						"email": "test@gmail.com",
						"firstName": "John"
					}
				"""))
			.andReturn();

		MockHttpServletRequestBuilder currentAuthUserRequest =
			MockMvcRequestBuilders
				.get("/auth/current")
				.cookie(mvcResult.getResponse().getCookies());

		mockMvc.perform(currentAuthUserRequest)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json("""
					{
						"email": "test@gmail.com",
						"firstName": "John"
					}
				"""));
	}
}