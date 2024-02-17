package com.ruchij.photo.album.web.config;

import com.ruchij.photo.album.web.interceptors.AuthenticationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {
	private final AuthenticationInterceptor authenticationInterceptor;

	public AppConfig(AuthenticationInterceptor authenticationInterceptor) {
		this.authenticationInterceptor = authenticationInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authenticationInterceptor);
	}
}
