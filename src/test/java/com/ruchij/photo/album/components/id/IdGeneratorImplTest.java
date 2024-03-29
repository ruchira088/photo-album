package com.ruchij.photo.album.components.id;

import org.junit.jupiter.api.Test;

import static com.ruchij.photo.album.components.id.IdGeneratorImpl.kebabCase;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IdGeneratorImplTest {

	@Test
	void kebabCaseShouldReturnExpectedResult() {
		assertEquals("my-album", kebabCase("MyAlbum"));
		assertEquals("album", kebabCase("Album"));
	}
}