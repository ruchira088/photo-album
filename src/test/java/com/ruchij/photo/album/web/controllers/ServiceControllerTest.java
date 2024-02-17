package com.ruchij.photo.album.web.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Clock;
import java.time.Instant;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private Clock clock;

	@Test
	public void shouldReturnSystemInformation() throws Exception {
		Mockito.when(clock.instant()).thenReturn(Instant.parse("2024-02-17T04:58:47.474071Z"));

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/service/info");

		mockMvc.perform(requestBuilder)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(content().json("""
    				{ "serviceName": "photo-album", "currentTimestamp": "2024-02-17T04:58:47.474071Z" }
				"""));
	}
}