package com.stream.lights.StreamLights.model.http.twitch;

import com.stream.lights.StreamLights.model.http.twitch.TwitchWebhookRequest.WebhookSubscription;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TwitchWebhookRequestTest {

	@Test
	void twitchWebhookRequest_getSetFields_success() {
		final TwitchWebhookRequest request = new TwitchWebhookRequest();
		final WebhookSubscription sub = new WebhookSubscription();
		final TwitchEvent e = new TwitchEvent();

		request.setChallenge("challenge");
		request.setSubscription(sub);
		request.setEvent(e);

		assertEquals("challenge", request.getChallenge());
		assertEquals(sub, request.getSubscription());
		assertEquals(e, request.getEvent());
	}
}
