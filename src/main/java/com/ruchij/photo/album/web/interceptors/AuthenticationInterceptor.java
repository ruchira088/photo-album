package com.ruchij.photo.album.web.interceptors;

import com.ruchij.photo.album.web.exceptions.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
	@Value("${application.authentication.secret}")
	private String authenticationSecret;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;

			boolean isPublicEndpoint = handlerMethod.hasMethodAnnotation(PublicEndpoint.class);

			if (isPublicEndpoint || isAuthenticated(request)) {
				return true;
			} else {
				throw new AuthenticationException();
			}
		} else {
			return true;
		}
	}

	boolean isAuthenticated(HttpServletRequest httpServletRequest) {
		String authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader == null) {
			return false;
		} else {
			Pattern pattern = Pattern.compile("Bearer (.+)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(authorizationHeader);

			if (matcher.find()) {
				String token = matcher.group(1);
				boolean isMatch = authenticationSecret.equalsIgnoreCase(token);
				return isMatch;
			} else {
				return false;
			}
		}
	}
}
