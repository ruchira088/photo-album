package com.ruchij.photo.album.daos.flyway;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity(name = "flyway_schema_history")
public class FlywaySchema {
	@Id
	@Column(name = "installed_rank")
	private Integer installedRank;

	@Column(name = "version")
	private String version;

	@Column(name = "description")
	private String description;

	@Column(name = "script")
	private String script;

	@Column(name = "checksum")
	private Integer checksum;

	@Column(name = "installed_on")
	private Instant installedOn;

	@Column(name = "success")
	private Boolean success;

	public Integer getInstalledRank() {
		return installedRank;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public String getScript() {
		return script;
	}

	public Integer getChecksum() {
		return checksum;
	}

	public Instant getInstalledOn() {
		return installedOn;
	}

	public Boolean getSuccess() {
		return success;
	}
}
