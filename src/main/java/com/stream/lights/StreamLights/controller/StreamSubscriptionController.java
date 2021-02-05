package com.stream.lights.StreamLights.controller;

import com.stream.lights.StreamLights.model.http.twitch.CreateSubscriptionRequest;
import com.stream.lights.StreamLights.service.TwitchService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StreamSubscriptionController {

	@NonNull
	private final TwitchService twitchService; // TODO final may prevent proper mocking

	@PostMapping(value = "/twitch/subscription/create", produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> createSubscription(@RequestBody final CreateSubscriptionRequest request) {
		return twitchService.subscribe(request, "token");
	}
}
