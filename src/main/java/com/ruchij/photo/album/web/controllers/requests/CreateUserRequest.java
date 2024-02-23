package com.ruchij.photo.album.web.controllers.requests;

import java.util.Optional;

public record CreateUserRequest(String email, String password, String firstName, Optional<String> lastName) {
}
