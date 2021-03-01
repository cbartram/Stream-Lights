package com.stream.lights.StreamLights.util;

import com.google.common.base.Charsets;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
	 * Creates a base64 encoded version of the username and password to be sent through HTTP headers
	 * in the Authorization header.
	 * @param username String username The username in the credentials
	 * @param password String password the password in the credentials
	 * @return String the fully base64 encoded string ready to be passed through the authorization header (including)
	 * the "Basic " part of the header.
	 */
	public static String toBasicAuth(String username, String password) {
		if(username == null) {
			username = "username";
		}

		if(password == null) {
			password = "password";
		}

		Charset charset = StandardCharsets.ISO_8859_1;
		CharsetEncoder encoder = charset.newEncoder();
		if (encoder.canEncode(username) && encoder.canEncode(password)) {
			String credentialsString = username + ":" + password;
			byte[] encodedBytes = Base64.getEncoder().encode(credentialsString.getBytes(charset));
			return "Basic " + new String(encodedBytes, charset);
		} else {
			throw new IllegalArgumentException("Username or password contains characters that cannot be encoded to " + charset.displayName());
		}
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
