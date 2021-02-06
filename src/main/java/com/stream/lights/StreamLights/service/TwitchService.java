package com.stream.lights.StreamLights.service;

import com.stream.lights.StreamLights.model.http.twitch.CreateSubscriptionRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * TwitchService - Service class which wraps the Twitch Http API for creating managing subscriptions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TwitchService {

	@NonNull
	private final RestTemplate restTemplate; // TODO final here may prevent proper mocking

	@NonNull
	private final TwitchAuthService twitchAuthService;

	@Value("${twitch.host.api}")
	private String twitchHost;

	@Value("${twitch.url.subscription.create}")
	private String twitchSubscriptionUrl;

	@Value("${twitch.client.id}")
	private String clientId;

	/**
	 * Creates a new subscription to the Twitch API to listen for in-stream events
	 * @param request CreateSubscriptionRequest request object to be added to the POST request body
	 * @return ResponseEntity String based response entity containing the body of the response as well as the status code.
	 */
	public ResponseEntity<String> subscribe(@NonNull final CreateSubscriptionRequest request) {
		log.info("Attempting to make POST to {}{} to create a new subscription.", twitchHost, twitchSubscriptionUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(twitchAuthService.getAppAccessToken());
		headers.set("Client-ID", clientId);
		log.info("Create subscription object: {}", request);

		HttpEntity<CreateSubscriptionRequest> entity = new HttpEntity<>(request, headers);
		log.info("Create subscription request POST Body Json: {}", entity);

		ResponseEntity<String> response = restTemplate.exchange(twitchHost + twitchSubscriptionUrl, HttpMethod.POST, entity, String.class);
		log.info("Response status code = {} and body = {}", response.getStatusCode(), response.getBody());
		return response;
	}
}
