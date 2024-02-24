package com.ruchij.photo.album;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.time.Clock;
import java.util.Properties;

@SpringBootApplication
public class PhotoAlbumApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoAlbumApplication.class, args);
	}

	@Bean
	Clock clock() {
		return Clock.systemDefaultZone();
	}

	@Bean
	PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer =
			new PropertySourcesPlaceholderConfigurer();

		propertySourcesPlaceholderConfigurer.setLocation(new ClassPathResource("git.properties"));

		return propertySourcesPlaceholderConfigurer;
	}


	@Bean
	Properties properties() {
		return System.getProperties();
	}

}
