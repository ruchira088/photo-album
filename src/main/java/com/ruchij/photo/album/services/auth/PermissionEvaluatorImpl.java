package com.ruchij.photo.album.services.auth;

import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.album.AlbumRepository;
import com.ruchij.photo.album.daos.photo.PhotoRepository;
import com.ruchij.photo.album.daos.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.util.Optional;

@Component
public class PermissionEvaluatorImpl implements PermissionEvaluator {
	private static final String ALBUM_PASSWORD_HEADER = "X-Album-Password";

	enum Permission {
		READ, WRITE;

		static Permission parse(Object permission) {
			if ("WRITE".equalsIgnoreCase(permission.toString())) {
				return WRITE;
			} else {
				return READ;
			}
		}
	}

	private final AlbumRepository albumRepository;
	private final PhotoRepository photoRepository;
	private final PasswordEncoder passwordEncoder;

	public PermissionEvaluatorImpl(AlbumRepository albumRepository, PhotoRepository photoRepository, PasswordEncoder passwordEncoder) {
		this.albumRepository = albumRepository;
		this.photoRepository = photoRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		return false;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

		return hasPermission(httpServletRequest, authentication, targetId, targetType, permission);
	}

	boolean hasPermission(HttpServletRequest httpServletRequest, Authentication authentication, Serializable targetId, String targetType, Object permission) {
		Optional<String> albumPasswordOptional = Optional.ofNullable(httpServletRequest.getHeader(ALBUM_PASSWORD_HEADER));

		Optional<User> userOptional =
			authentication.getPrincipal() instanceof User ? Optional.of((User) authentication.getPrincipal()) : Optional.empty();

		Permission permissionType = Permission.parse(permission);

		return switch (targetType) {
			case "ALBUM" -> albumRepository.findById(targetId.toString())
				.filter(album ->
					hasPermissionToAlbum(album, permissionType, userOptional, albumPasswordOptional)
				)
				.isPresent();

			case "PHOTO" -> photoRepository.findById(targetId.toString())
				.filter(photo ->
					hasPermissionToAlbum(photo.getAlbum(), permissionType, userOptional, albumPasswordOptional)
				)
				.isPresent();

			default -> throw new IllegalStateException("Unexpected value: " + targetType);
		};
	}

	private boolean hasPermissionToAlbum(
		Album album,
		Permission permission,
		Optional<User> userOptional,
		Optional<String> albumPasswordOptional
	) {
		if (permission == Permission.WRITE) {
			return userOptional.map(User::getId).equals(Optional.of(album.getUser().getId()));
		} else {
			boolean isPublic = album.getPublic();

			boolean isUserAlbum = (userOptional.filter(user ->
				user.getId().equalsIgnoreCase(album.getUser().getId())).isPresent());

			boolean isAlbumPasswordMatch =
				albumPasswordOptional.flatMap(albumPassword ->
						album.getAlbumPassword().filter(savedAlbumPassword ->
							passwordEncoder.matches(albumPassword, savedAlbumPassword.getHashedPassword())
						)
					)
					.isPresent();

			return isPublic || isUserAlbum || isAlbumPasswordMatch;
		}
	}
}
