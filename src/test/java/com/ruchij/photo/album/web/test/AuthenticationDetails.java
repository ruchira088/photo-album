package com.ruchij.photo.album.web.test;

import com.ruchij.photo.album.daos.user.User;
import jakarta.servlet.http.Cookie;

public record AuthenticationDetails(User user, Cookie[] cookies) {
}
