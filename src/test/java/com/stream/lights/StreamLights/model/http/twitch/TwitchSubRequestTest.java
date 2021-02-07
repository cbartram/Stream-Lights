package com.stream.lights.StreamLights.model.http.twitch;

import com.stream.lights.StreamLights.model.http.twitch.TwitchSubRequest.SubscriptionCondition;
import com.stream.lights.StreamLights.model.http.twitch.TwitchSubRequest.SubscriptionTransport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TwitchSubRequestTest {

	@Test
	void twitchSubRequest_getSetFields_success() {
		final TwitchSubRequest request = new TwitchSubRequest();
		final SubscriptionCondition condition = new SubscriptionCondition();
		final SubscriptionTransport transport = new SubscriptionTransport();

		condition.setBroadcasterUserId("buid");
		condition.setUsername("username");

		transport.setMethod("method");
		transport.setSecret("secret");
		transport.setCallback("callback");

		request.setCondition(condition);
		request.setTransport(transport);
		request.setType("type");
		request.setVersion("version");

		assertEquals(condition, request.getCondition());
		assertEquals(transport, request.getTransport());
		assertEquals("type", request.getType());
		assertEquals("version", request.getVersion());
	}
}
