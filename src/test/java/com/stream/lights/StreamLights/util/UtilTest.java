package com.stream.lights.StreamLights.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class UtilTest {

	@Test
	void util_randomString_generatesRandomStringOfCorrectSize() {
		String s = Util.randomString(10);
		log.info(s);
		assertEquals(10, s.length());
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
