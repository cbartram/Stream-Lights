package com.stream.lights.StreamLights.controller;

import com.stream.lights.StreamLights.TestUtils;
import com.stream.lights.StreamLights.model.http.twitch.TwitchWebhookRequest;
import com.stream.lights.StreamLights.service.twitch.TwitchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
class StreamSubscriptionControllerTest {

	@InjectMocks
	private StreamSubscriptionController controller;

	@Mock
	private TwitchService twitchService;

	@Test
	void streamSubscriptionController_createSubscription_success() {
		MockitoAnnotations.openMocks(this);
		when(twitchService.fetchTwitchUserId(any())).thenReturn(TestUtils.initSubscriptionCondition());
		when(twitchService.subscribe(any())).thenReturn(ResponseEntity.ok(TestUtils.TWITCH_SUBSCRIPTION_SUCCESS_JSON_RESPONSE));

		ResponseEntity<String> response = controller.createSubscription(TestUtils.initTwitchSubRequest());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(TestUtils.TWITCH_SUBSCRIPTION_SUCCESS_JSON_RESPONSE, response.getBody());
	}


	@Test
	void streamSubscriptionController_listSubscriptions_success() {
		MockitoAnnotations.openMocks(this);
		when(twitchService.listSubscriptions()).thenReturn(ResponseEntity.ok(TestUtils.TWITCH_LIST_SUBSCRIPTION_JSON_RESPONSE));

		ResponseEntity<String> response = controller.listSubscriptions();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(TestUtils.TWITCH_LIST_SUBSCRIPTION_JSON_RESPONSE, response.getBody());
	}


	@Test
	void streamSubscriptionController_deleteSubscription_success() {
		MockitoAnnotations.openMocks(this);
		when(twitchService.deleteSubscription(any())).thenReturn(ResponseEntity.ok("{ \"deleted\": \"id\" }"));

		ResponseEntity<String> response = controller.deleteSubscription("id");
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("{ \"deleted\": \"id\" }", response.getBody());
	}


	@Test
	void streamSubscriptionController_webhookCallback_verifyChallenge_success() {
		MockitoAnnotations.openMocks(this);

		ResponseEntity<String> response = controller.webhook(TestUtils.initTwitchWebhookRequest());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("challenge_id", response.getBody());
	}


	@Test
	void streamSubscriptionController_webhookCallback_standardEvent_success() {
		MockitoAnnotations.openMocks(this);
		final TwitchWebhookRequest subscription = TestUtils.initTwitchWebhookRequest();
		subscription.setChallenge(null);
		subscription.setEvent(TestUtils.initTwitchEvent());

		ResponseEntity<String> response = controller.webhook(subscription);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("success", response.getBody());
	}

}
