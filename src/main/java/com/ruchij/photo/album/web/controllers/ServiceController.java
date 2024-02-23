package com.ruchij.photo.album.web.controllers;

import com.ruchij.photo.album.services.models.HealthCheck;
import com.ruchij.photo.album.services.models.ServiceInformation;
import com.ruchij.photo.album.services.monitoring.MonitoringService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/service", produces = MediaType.APPLICATION_JSON_VALUE)
public class ServiceController {
	private final MonitoringService monitoringService;

	public ServiceController(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	@ResponseBody
	@GetMapping("/info")
	public ServiceInformation info() {
		return monitoringService.getServiceInformation();
	}

	@GetMapping("/health-check")
	public ResponseEntity<HealthCheck> healthCheck() {
		HealthCheck healthCheck = monitoringService.performHealthCheck();
		HttpStatus status = healthCheck.isHealthy() ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;

		return ResponseEntity.status(status).body(healthCheck);
	}
}