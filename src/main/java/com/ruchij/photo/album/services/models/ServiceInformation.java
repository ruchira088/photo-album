package com.ruchij.photo.album.services.models;

public record ServiceInformation(
	String serviceName,
	String serviceVersion,
	String currentTimestamp,
	String javaVersion,
	String gitBranch,
	String gitCommit,
	String buildTimestamp
) {
}
