package com.stream.lights.StreamLights.util;

import java.util.Random;

/**
 * Util.java - Class containing useful utility methods which can be called from a static context.
 */
public class Util {


	/**
	 * Generates a random string from [A-Z][0-9] given a length as input
	 * @param length int length of the random string
	 * @return String randomized string
	 */
	public static String randomString(int length) {
		final String SALT_CHARS = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890";
		char[] salt = new char[length];
		int i = 0;
		Random rnd = new Random();
		while (i < length) {
			salt[i] = SALT_CHARS.charAt(rnd.nextInt(SALT_CHARS.length()));
			i++;
		}
		return String.valueOf(salt);
	}
}
