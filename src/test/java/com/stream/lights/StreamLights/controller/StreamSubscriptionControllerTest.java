package com.stream.lights.StreamLights.controller;

import com.stream.lights.StreamLights.TestUtils;
import com.stream.lights.StreamLights.service.TwitchAuthService;
import com.stream.lights.StreamLights.service.TwitchService;
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

@SpringBootTest
class StreamSubscriptionControllerTest {

	@InjectMocks
	private StreamSubscriptionController controller;

	@Mock
	private TwitchService twitchService;

	@Mock
	private TwitchAuthService authService;

	@Test
	void streamSubscriptionController_createSubscription_success() {
		MockitoAnnotations.openMocks(this);
		when(twitchService.fetchTwitchUserId(any())).thenReturn(TestUtils.initSubscriptionCondition());
		when(twitchService.subscribe(any())).thenReturn(ResponseEntity.ok(TestUtils.TWITCH_SUBSCRIPTION_SUCCESS_JSON_RESPONSE));

		ResponseEntity<String> response = controller.createSubscription(TestUtils.initTwitchSubRequest());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(TestUtils.TWITCH_SUBSCRIPTION_SUCCESS_JSON_RESPONSE, response.getBody());
	}

}
