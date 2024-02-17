package com.ruchij.photo.album.services.monitoring;

import com.ruchij.photo.album.services.models.BuildInformation;
import com.ruchij.photo.album.services.models.ServiceInformation;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Properties;

@Service
public class MonitoringServiceImpl implements MonitoringService {
	private static final String SERVICE_NAME = "photo-album";

	private final String serviceVersion;
	private final String javaVersion;
	private final String gitBranch;
	private final String gitCommit;
	private final String buildTimestamp;

	private final Clock clock;

	public MonitoringServiceImpl(BuildInformation buildInformation, Properties properties, Clock clock) {
		this.serviceVersion = buildInformation.getBuildVersion();
		this.gitBranch = buildInformation.getGitBranch();
		this.gitCommit = buildInformation.getGitCommit();
		this.javaVersion = properties.getProperty("java.version", "unknown");
		this.buildTimestamp = buildInformation.getBuildTimestamp();
		this.clock = clock;
	}

	@Override
	public ServiceInformation getServiceInformation() {
		return new ServiceInformation(
			SERVICE_NAME,
			serviceVersion,
			clock.instant(),
			javaVersion,
			gitBranch,
			gitCommit,
			buildTimestamp
		);
	}
}
