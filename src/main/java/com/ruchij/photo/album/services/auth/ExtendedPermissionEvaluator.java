package com.ruchij.photo.album.services.auth;

import com.ruchij.photo.album.daos.album.Album;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.PermissionEvaluator;

public interface ExtendedPermissionEvaluator extends PermissionEvaluator {
	Album authenticateAlbum(String albumId, String password, HttpSession httpSession);
}
