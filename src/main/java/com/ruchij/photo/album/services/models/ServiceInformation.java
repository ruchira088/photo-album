package com.ruchij.photo.album.services.models;

import java.time.Instant;

public record ServiceInformation(
	String serviceName,
	String serviceVersion,
	Instant currentTimestamp,
	String javaVersion,
	String gitBranch,
	String gitCommit,
	String buildTimestamp
) {
}
