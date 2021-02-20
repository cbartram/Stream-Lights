package com.stream.lights.StreamLights.model.dynamodb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HueBridgeCredentialsTest {

	@Test
	void hueBridgeCredentials_getSetFields_success() {
		final HueBridgeCredentials credentials = new HueBridgeCredentials();
		credentials.setRefreshToken("refresh");
		credentials.setAccessToken("access");
		credentials.setPartitionKey("partition");
		credentials.setSortId("sort");
		credentials.setHueApiKey("api");

		assertEquals("refresh", credentials.getRefreshToken());
		assertEquals("access", credentials.getAccessToken());
		assertEquals("partition", credentials.getPartitionKey());
		assertEquals("sort", credentials.getSortId());
		assertEquals("api", credentials.getHueApiKey());
	}
}
