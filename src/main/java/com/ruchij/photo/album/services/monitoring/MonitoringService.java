package com.ruchij.photo.album.services.monitoring;

import com.ruchij.photo.album.daos.flyway.FlywaySchema;
import com.ruchij.photo.album.services.models.HealthCheck;
import com.ruchij.photo.album.services.models.ServiceInformation;

import java.util.List;

public interface MonitoringService {
	ServiceInformation getServiceInformation();

	HealthCheck performHealthCheck();

	List<FlywaySchema> flywaySchemas();
}
