package com.stream.lights.StreamLights.model.http.auth;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OAuthResponseTest {

	@Test
	void oauthResponse_getSetFields_success() {
		final OAuthResponse response = new OAuthResponse();
		response.setAccessToken("token");
		response.setExpiresIn(1);
		response.setRefreshToken("refresh");
		response.setScope(Collections.emptyList());
		response.setTokenType("type");

		assertEquals("token", response.getToken());
		assertEquals(1, response.getExpiresIn());
		assertEquals("refresh", response.getRefreshToken());
		assertEquals(Collections.emptyList(), response.getScope());
		assertEquals("type", response.getTokenType());
	}
}
