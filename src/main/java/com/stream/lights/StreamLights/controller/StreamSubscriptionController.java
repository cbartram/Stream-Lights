package com.stream.lights.StreamLights.controller;

import com.stream.lights.StreamLights.model.http.twitch.CreateSubscriptionRequest;
import com.stream.lights.StreamLights.model.http.twitch.SubscriptionWebhookRequest;
import com.stream.lights.StreamLights.service.TwitchService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * StreamSubscriptionController - Controller class which manages the creation of
 * new subscriptions and validation of subscriptions via the callback endpoint
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class StreamSubscriptionController {

	@NonNull
	private final TwitchService twitchService; // TODO final may prevent proper mocking

	@PostMapping(value = "/twitch/subscription/create", produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> createSubscription(@RequestBody final CreateSubscriptionRequest request) {
		return twitchService.subscribe(request);
	}

	@PostMapping(value = "/twitch/subscription/webhook", produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> webhook(@RequestBody final SubscriptionWebhookRequest request) {
		log.info("Received new request to webhook callback endpoint: {}", request);
		return ResponseEntity.of(Optional.of("Ok"));
	}
}
