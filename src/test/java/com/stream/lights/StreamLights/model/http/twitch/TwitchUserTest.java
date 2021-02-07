package com.stream.lights.StreamLights.model.http.twitch;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TwitchUserTest {

	@Test
	void twitchUser_getSetFields_success() {
		final TwitchUser user = new TwitchUser();
		user.setBroadcasterType("type");
		user.setCreatedAt(LocalDateTime.MAX);
		user.setDescription("desc");
		user.setDisplayName("display");
		user.setId("id");
		user.setLogin("login");
		user.setOfflineProfilePicture("pic");
		user.setProfilePicture("pic");
		user.setType("type");
		user.setViewCount(1);

		assertEquals("type", user.getBroadcasterType());
		assertEquals(LocalDateTime.MAX, user.getCreatedAt());
		assertEquals("desc", user.getDescription());
		assertEquals("display", user.getDisplayName());
		assertEquals("id", user.getId());
		assertEquals("login", user.getLogin());
		assertEquals("pic", user.getOfflineProfilePicture());
		assertEquals("pic", user.getProfilePicture());
		assertEquals("type", user.getType());
		assertEquals(1, user.getViewCount());
	}
}
