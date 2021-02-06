package com.stream.lights.StreamLights.controller;

import com.stream.lights.StreamLights.model.http.twitch.TwitchSubRequest;
import com.stream.lights.StreamLights.model.http.twitch.TwitchSubRequest.SubscriptionCondition;
import com.stream.lights.StreamLights.model.http.twitch.TwitchWebhookRequest;
import com.stream.lights.StreamLights.service.TwitchService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@PostMapping(value = "/twitch/subscription", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createSubscription(@RequestBody final TwitchSubRequest request) {
		log.info("Looking up Twitch Id for user: {}", request.getCondition().getUsername());
		final SubscriptionCondition id = twitchService.fetchTwitchUserId(request.getCondition().getUsername());
		log.info("Found twitch id for user: {}={}", request.getCondition().getUsername(), id);
		request.setCondition(id);
		return twitchService.subscribe(request);
	}

	@GetMapping(value = "/twitch/subscription", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> listSubscriptions() {
		return twitchService.listSubscriptions();
	}

	@DeleteMapping(value = "/twitch/subscription/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteSubscription(@PathVariable final String subscriptionId) {
		return twitchService.deleteSubscription(subscriptionId);
	}

	@PostMapping(value = "/twitch/subscription/webhook", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> webhook(@RequestBody final TwitchWebhookRequest request) {
		if(request.getChallenge() != null) {
			log.info("Received new webhook validation request {}. Responding with challenge: {}", request, request.getChallenge());
			return ResponseEntity.of(Optional.of(request.getChallenge()));
		}

		log.info("Received new event on webhook endpoint. Event = {}", request);
		// TODO Notify philips hue lights of the event!
		return ResponseEntity.of(Optional.of("success"));
	}
}
