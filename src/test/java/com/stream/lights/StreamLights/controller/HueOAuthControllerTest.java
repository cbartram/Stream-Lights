package com.stream.lights.StreamLights.controller;

import com.stream.lights.StreamLights.TestUtils;
import com.stream.lights.StreamLights.model.dynamodb.HueBridgeCredentials;
import com.stream.lights.StreamLights.service.hue.HueService;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class HueOAuthControllerTest {

	@InjectMocks
	private HueOAuthController controller;

	@Mock
	private HueService hueService;

	@Mock
	private DynamoDbTable<HueBridgeCredentials> table;

	@Test
	void streamSubscriptionController_createSubscription_success() {
		MockitoAnnotations.openMocks(this);
		when(hueService.linkBridge(any())).thenReturn(TestUtils.initHueCredentials());

		ResponseEntity<String> response = controller.createSubscription(TestUtils.initTwitchSubRequest());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(TestUtils.TWITCH_SUBSCRIPTION_SUCCESS_JSON_RESPONSE, response.getBody());
	}
}
