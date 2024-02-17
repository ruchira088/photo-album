package com.ruchij.photo;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Playground {
	@Test
	void play() {
		Pattern pattern = Pattern.compile("Bearer (.+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher("beasrer hello");
		System.out.println(matcher.find());
		System.out.println(matcher.group(1));
	}
}
