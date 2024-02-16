package com.ruchij.photo.album.components.id;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGeneratorImpl implements IdGenerator {

	static String kebabCase(String input) {
		StringBuilder stringBuilder = new StringBuilder();

		for (char character : input.toCharArray()) {
			if (Character.isUpperCase(character)) {
				if (stringBuilder.isEmpty()) {
					stringBuilder.append(Character.toLowerCase(character));
				} else {
					stringBuilder.append('-');
					stringBuilder.append(Character.toLowerCase(character));
				}
			} else {
				stringBuilder.append(character);
			}
		}

		return stringBuilder.toString();
	}

	@Override
	public String generateId(Class<?> clazz) {
		return kebabCase(clazz.getSimpleName()) + "-" + UUID.randomUUID();
	}
}
