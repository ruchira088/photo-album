package com.ruchij.photo.album.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruchij.photo.album.web.exceptions.ErrorMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class WebSecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, SecurityContextRepository securityContextRepository, ObjectMapper objectMapper) throws Exception {
		httpSecurity.csrf(AbstractHttpConfigurer::disable)
			.exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
				httpSecurityExceptionHandlingConfigurer
					.authenticationEntryPoint(authenticationEntryPoint(objectMapper))
			)
			.cors(Customizer.withDefaults())
			.securityContext(
				securityContext -> securityContext.securityContextRepository(securityContextRepository)
			)
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers(HttpMethod.POST, "/album").authenticated()
				.requestMatchers("/album/user").authenticated()
				.anyRequest().permitAll()
			);

		return httpSecurity.build();
	}

	private AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
		return (request, response, authException) -> {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			objectMapper.writeValue(response.getOutputStream(), new ErrorMessage(List.of(authException.getMessage())));
		};
	}

	@Bean
	public SecurityContextRepository securityContextRepository() {
		return new HttpSessionSecurityContextRepository();
	}

	@Bean
	public SecurityContextHolderFilter securityContextHolderFilter(SecurityContextRepository securityContextRepository) {
		return new SecurityContextHolderFilter(securityContextRepository);
	}

	@Bean
	public AuthenticationManager authenticationManager(
		UserDetailsService userDetailsService, PasswordEncoder passwordEncoder
	) {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(daoAuthenticationProvider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public LogoutHandler logoutHandler() {
		return new SecurityContextLogoutHandler();
	}

	@Bean
	public MethodSecurityExpressionHandler methodSecurityExpressionHandler(PermissionEvaluator permissionEvaluator) {
		DefaultMethodSecurityExpressionHandler securityExpressionHandler = new DefaultMethodSecurityExpressionHandler();
		securityExpressionHandler.setPermissionEvaluator(permissionEvaluator);
		return securityExpressionHandler;
	}
}
