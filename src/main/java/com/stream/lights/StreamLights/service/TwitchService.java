package com.stream.lights.StreamLights.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stream.lights.StreamLights.model.http.twitch.TwitchSubRequest;
import com.stream.lights.StreamLights.model.http.twitch.TwitchSubRequest.SubscriptionCondition;
import com.stream.lights.StreamLights.model.http.twitch.TwitchUser;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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

	@NonNull
	private final ObjectMapper mapper;

	@Value("${twitch.host.api}")
	private String twitchHost;

	@Value("${twitch.url.subscription.create}")
	private String twitchSubscriptionUrl;

	@Value("${twitch.url.users.login}")
	private String twitchUserLoginEndpoint;

	@Value("${twitch.client.id}")
	private String clientId;

	private final Map<String, String> usernameCache = new ConcurrentHashMap<>();

	/**
	 * Fetches a user's unique id given their Twitch username
	 * @param username String username
	 * @return String the users unique twitch id.
	 */
	public TwitchSubRequest.SubscriptionCondition fetchTwitchUserId(final String username) {
		final SubscriptionCondition condition = new SubscriptionCondition();
		if(usernameCache.containsKey(username)) {
			log.info("Twitch User id cache contains key: {}", username);
			condition.setBroadcasterUserId(usernameCache.get(username));
			return condition;
		}

		log.info("Attempting to make GET to {}{} to fetch user id for username = {}.", twitchHost, twitchUserLoginEndpoint, username);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(twitchAuthService.getAppAccessToken());
		headers.set("Client-ID", clientId);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(twitchHost + twitchUserLoginEndpoint)
				.queryParam("login", username);

		HttpEntity<?> entity = new HttpEntity<>(headers);
		HttpEntity<String> response = restTemplate.exchange(
				builder.toUriString(),
				HttpMethod.GET,
				entity,
				String.class);

		log.info("Fetch twitch username response body = {}", response.getBody());
		try {
			Map<String, List<TwitchUser>> twitchUserMap = mapper.readValue(response.getBody(), Map.class);
			List<TwitchUser> users = twitchUserMap.get("data");

			if(!users.isEmpty()) {
				final String userId = users.get(0).getId();
				usernameCache.put(username, userId);
				condition.setBroadcasterUserId(userId);
				return condition;
			} else {
				log.error("Could not find Twitch user details for the username: {}", username);
				return null;
			}
		} catch(JsonProcessingException e) {
			log.error("There was an error trying to map Json string: {} to the Map.class. ", response.getBody(), e);
			return null;
		}
	}


	/**
	 * Lists the current subscriptions the service has created.
	 * @return ResponseEntity String response entity containing the subscriptions currently made
	 */
	public ResponseEntity<String> listSubscriptions() {
		log.info("Attempting to make GET to {}{} to list current subscription.", twitchHost, twitchSubscriptionUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(twitchAuthService.getAppAccessToken());
		headers.set("Client-ID", clientId);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(twitchHost + twitchSubscriptionUrl, HttpMethod.GET, entity, String.class);
		log.info("List subscription response status code = {} and body = {}", response.getStatusCode(), response.getBody());
		return response;
	}

	/**
	 * Deletes an existing subscription given the unique subscription id.
	 * @param subscriptionId String the id of the subscription to delete
	 * @return String json representing if the deletion was successful or not.
	 */
	public ResponseEntity<String> deleteSubscription(@NonNull final String subscriptionId) {
		log.info("Attempting to make DELETE to {}{} to remove subscription id = {}", twitchHost, twitchSubscriptionUrl, subscriptionId);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(twitchAuthService.getAppAccessToken());
		headers.set("Client-ID", clientId);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(twitchHost + twitchSubscriptionUrl)
				.queryParam("id", subscriptionId);

		restTemplate.exchange(
				builder.toUriString(),
				HttpMethod.DELETE,
				entity,
				String.class);
		return ResponseEntity.of(Optional.of("{ \"deleted\": \"" + subscriptionId + "\" }"));
	}

	/**
	 * Creates a new subscription to the Twitch API to listen for in-stream events
	 * @param request CreateSubscriptionRequest request object to be added to the POST request body
	 * @return ResponseEntity String based response entity containing the body of the response as well as the status code.
	 */
	public ResponseEntity<String> subscribe(@NonNull final TwitchSubRequest request) {
		log.info("Attempting to make POST to {}{} to create a new subscription.", twitchHost, twitchSubscriptionUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(twitchAuthService.getAppAccessToken());
		headers.set("Client-ID", clientId);
		log.info("Create subscription object: {}", request);

		HttpEntity<TwitchSubRequest> entity = new HttpEntity<>(request, headers);
		log.info("Create subscription request POST Body Json: {}", entity);

		ResponseEntity<String> response = restTemplate.exchange(twitchHost + twitchSubscriptionUrl, HttpMethod.POST, entity, String.class);
		log.info("Response status code = {} and body = {}", response.getStatusCode(), response.getBody());
		return response;
	}
}
