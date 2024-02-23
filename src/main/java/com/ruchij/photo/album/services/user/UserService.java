package com.ruchij.photo.album.services.user;

import com.ruchij.photo.album.daos.user.User;
import com.ruchij.photo.album.services.exceptions.ResourceConflictException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
	User create(String email, String password, String firstName, Optional<String> lastName) throws ResourceConflictException;
}
