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

	/**
	 * Masks a sensitive value in the logs with asterisks.
	 * @param string String the value to mask
	 * @param numCharsRevealed int The number of characters to reveal in the string.
	 * @return String a masked version of the string. I.e snake, 3 => sna**
	 */
	public static String mask(final String string, final int numCharsRevealed) {
		if(numCharsRevealed < 0 || numCharsRevealed > string.length()) {
			return "*".repeat(string.length());
		}

		String subString = string.substring(0, numCharsRevealed);
		subString += "*".repeat(string.length() - subString.length());
		return subString;
	}
}
