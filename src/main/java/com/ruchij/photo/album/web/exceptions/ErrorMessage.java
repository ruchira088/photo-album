package com.ruchij.photo.album.web.exceptions;

import java.util.List;

public record ErrorMessage(List<String> errors) {
}
