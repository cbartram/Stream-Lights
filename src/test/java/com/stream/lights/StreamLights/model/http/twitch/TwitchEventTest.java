package com.stream.lights.StreamLights.model.http.twitch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TwitchEventTest {

	@Test
	void twitchEvent_getSetFields_success() {
		final TwitchEvent event = new TwitchEvent();
		event.setBroadcasterUserId("biud");
		event.setBroadcasterUsername("bu");
		event.setUserId("u");
		event.setUserLogin("login");
		event.setUsername("uname");

		assertEquals("biud", event.getBroadcasterUserId());
		assertEquals("bu", event.getBroadcasterUsername());
		assertEquals("u", event.getUserId());
		assertEquals("login", event.getUserLogin());
		assertEquals("uname", event.getUsername());
	}
}
