package com.ruchij.photo.album.services.auth;

import com.ruchij.photo.album.daos.album.Album;
import com.ruchij.photo.album.daos.album.AlbumRepository;
import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.daos.photo.PhotoRepository;
import com.ruchij.photo.album.daos.user.User;
import com.ruchij.photo.album.services.exceptions.AuthorizationException;
import com.ruchij.photo.album.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class PermissionEvaluatorImpl implements ExtendedPermissionEvaluator {
	private static final String AUTHENTICATED_ALBUMS = "AUTHENTICATED_ALBUMS";

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

	@Override
	public Album authenticateAlbum(String albumId, String password, HttpSession httpSession) {
		Album album = albumRepository.findById(albumId).orElseThrow(() -> new ResourceNotFoundException(albumId, Album.class));

		boolean isAuthenticatedWithAlbumPassword =
			album.getAlbumPassword()
				.filter(albumPassword -> passwordEncoder.matches(password, albumPassword.getHashedPassword()))
				.isPresent();

		if (isAuthenticatedWithAlbumPassword) {
			Set<String> authenticatedAlbums = (Set<String>) Optional.ofNullable(httpSession.getAttribute(AUTHENTICATED_ALBUMS)).orElse(new HashSet<>());
			authenticatedAlbums.add(albumId);
			httpSession.setAttribute(AUTHENTICATED_ALBUMS, authenticatedAlbums);
			return album;
		} else if (album.getPublic()) {
			return album;
		} else if (album.getAlbumPassword().isEmpty()) {
			throw new AuthorizationException("Private album: albumId=%s".formatted(albumId));
		} else {
			throw new AuthorizationException("Invalid password for albumId=%s".formatted(albumId));
		}
	}

	@Override
	public Set<Album> authenticatedAlbums(HttpSession httpSession) {
		Set<String> authenticatedAlbums = (Set<String>) Optional.ofNullable(httpSession.getAttribute(AUTHENTICATED_ALBUMS)).orElse(new HashSet<>());

		if (authenticatedAlbums.isEmpty()) {
			return Set.of();
		} else {
			List<Album> albums = albumRepository.getAlbumsByIdIsIn(List.copyOf(authenticatedAlbums));
			return Set.copyOf(albums);
		}
	}

	boolean hasPermission(HttpServletRequest httpServletRequest, Authentication authentication, Serializable targetId, String targetType, Object permission) {
		Optional<User> userOptional =
			authentication.getPrincipal() instanceof User ? Optional.of((User) authentication.getPrincipal()) : Optional.empty();

		Permission permissionType = Permission.parse(permission);

		return switch (targetType) {
			case "ALBUM" -> {
				Album album =
					albumRepository.findById(targetId.toString())
						.orElseThrow(() -> new ResourceNotFoundException(targetId.toString(), Album.class));

				yield hasPermissionToAlbum(album, permissionType, userOptional, httpServletRequest.getSession());
			}

			case "PHOTO" -> {
				Photo photo =
					photoRepository.findById(targetId.toString())
						.orElseThrow(() -> new ResourceNotFoundException(targetId.toString(), Photo.class));

				yield hasPermissionToAlbum(photo.getAlbum(), permissionType, userOptional, httpServletRequest.getSession());
			}

			default -> throw new IllegalStateException("Unexpected value: " + targetType);
		};
	}

	private boolean hasPermissionToAlbum(
		Album album,
		Permission permission,
		Optional<User> userOptional,
		HttpSession httpSession
	) {
		if (permission == Permission.WRITE) {
			return userOptional.map(User::getId).equals(Optional.of(album.getUser().getId()));
		} else {
			boolean isPublic = album.getPublic();

			boolean isUserAlbum = (userOptional.filter(user ->
				user.getId().equalsIgnoreCase(album.getUser().getId())).isPresent());

			Set<String> authenticatedAlbums =
				(Set<String>) Optional.ofNullable(httpSession.getAttribute(AUTHENTICATED_ALBUMS)).orElse(new HashSet<>());

			boolean isAuthenticatedAlbum = authenticatedAlbums.contains(album.getId());

			boolean hasPermission = isPublic || isUserAlbum || isAuthenticatedAlbum;

			if (!hasPermission) {
				if (album.getAlbumPassword().isEmpty()) {
					throw new AuthorizationException("%s is a private photo album".formatted(album.getId()));
				} else {
					throw new AuthorizationException("%s requires password to access".formatted(album.getId()));
				}
			}


			return true;
		}
	}
}
