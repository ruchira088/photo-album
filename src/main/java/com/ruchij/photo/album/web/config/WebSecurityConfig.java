package com.ruchij.photo.album.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class WebSecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, SecurityContextRepository securityContextRepository) throws Exception {
		httpSecurity.csrf(AbstractHttpConfigurer::disable)
			.cors(Customizer.withDefaults())
			.securityContext(
				securityContext -> securityContext.securityContextRepository(securityContextRepository)
			)
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers(HttpMethod.POST, "/album").authenticated()
				.anyRequest().permitAll()
			);

		return httpSecurity.build();
	}

	@Bean
	public SecurityContextRepository securityContextRepository() {
		return new HttpSessionSecurityContextRepository();
	};

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
