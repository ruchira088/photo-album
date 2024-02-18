package com.ruchij.photo.album.services.monitoring;

import com.ruchij.photo.album.services.models.HealthCheck;
import com.ruchij.photo.album.services.models.ServiceInformation;

public interface MonitoringService {
	ServiceInformation getServiceInformation();

	HealthCheck performHealthCheck();
}