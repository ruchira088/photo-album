package com.ruchij.photo.album.config;

import com.ruchij.photo.album.services.storage.StorageBackend;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "test.environment.github-actions", havingValue = "true")
public class GitHubActionsTestConfiguration {

	@Bean
	public StorageBackend storageBackend() {
		return new InMemoryStorageBackend();
	}
}
