package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.services.health.HealthService;
import com.ruchij.photo.album.services.models.ServiceInformation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
public class HealthController {
	private final HealthService healthService;

	public HealthController(HealthService healthService) {
		this.healthService = healthService;
	}

	@GetMapping("/info")
	public ServiceInformation info() {
		return healthService.getServiceInformation();
	}
}