package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.services.monitoring.MonitoringService;
import com.ruchij.photo.album.services.models.ServiceInformation;
import com.ruchij.photo.album.web.interceptors.PublicEndpoint;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/service", produces = MediaType.APPLICATION_JSON_VALUE)
public class ServiceController {
	private final MonitoringService monitoringService;

	public ServiceController(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	@PublicEndpoint
	@GetMapping("/info")
	public ServiceInformation info() {
		return monitoringService.getServiceInformation();
	}
}