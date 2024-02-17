package com.ruchij.photo.album.services.models;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BuildInformation {
	@Value("${git.build.version:unknown}")
	private String buildVersion;

	@Value("${git.commit.id.abbrev:unknown}")
	private String gitCommit;

	@Value("${git.branch:unknown}")
	private String gitBranch;

	@Value("${git.build.time:unknown}")
	private String buildTimestamp;

	public String getBuildVersion() {
		return buildVersion;
	}

	public String getGitCommit() {
		return gitCommit;
	}

	public String getGitBranch() {
		return gitBranch;
	}

	public String getBuildTimestamp() {
		return buildTimestamp;
	}
}
