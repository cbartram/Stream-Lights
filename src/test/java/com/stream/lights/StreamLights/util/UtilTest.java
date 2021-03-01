package com.stream.lights.StreamLights.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilTest {

	@Test
	void util_randomString_generatesRandomStringOfCorrectSize() {
		String s = Util.randomString(10);
		assertEquals(10, s.length());
	}

	@Test
	void util_toBasicAuth_returnsBasicAuthString() {
		final String auth = Util.toBasicAuth("foo", "bar");
		assertEquals("Basic Zm9vOmJhcg==", auth);
	}

	@Test
	void util_toBasicAuth_defaultsNullUsernameAndPassword() {
		final String auth = Util.toBasicAuth(null, null);
		assertEquals("Basic dXNlcm5hbWU6cGFzc3dvcmQ=", auth);
		final String auth2 = Util.toBasicAuth(null, "bar");
		assertEquals("Basic dXNlcm5hbWU6YmFy", auth2);
		final String auth3 = Util.toBasicAuth("foo", null);
		assertEquals("Basic Zm9vOnBhc3N3b3Jk", auth3);
	}


	@Test
	void util_toBasicAuth_illegalArgExceptionForIllegalCharacters() {
		try {
			Util.toBasicAuth("foo", "¬˚¡ª•åß∆˚¨¨∫≈˜˜Ò¡ªº//'''{}|+_)(!@#$%^&*()``");
		} catch(IllegalArgumentException e) {
			assertEquals("Username or password contains characters that cannot be encoded to ISO-8859-1", e.getMessage());
		}
	}


	@Test
	void util_mask_createsAMakedPassword() {
		String s = "iamasupersecretpassword";
		assertEquals("iamas******************", Util.mask(s, 5));
	}

	@Test
	void util_mask_returnsAllMaskedForShortRevealedChars() {
		String s = "iamasupersecretpassword";
		assertEquals("***********************", Util.mask(s, -1));
	}

	@Test
	void util_mask_returnsAllMaskedForLongRevealedChars() {
		String s = "iamasupersecretpassword";
		assertEquals("***********************", Util.mask(s, 2000));
	}
}
