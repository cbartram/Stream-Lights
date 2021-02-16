package com.stream.lights.StreamLights.service.twitch;


import com.google.common.base.Suppliers;
import com.stream.lights.StreamLights.model.http.auth.OAuthResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * TwitchAuthService - Service class which wraps Twitch API's to validate and aquire new
 * OAuth 2.0 access tokens for interacting with the Twitch API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TwitchAuthService {

	@Value("${twitch.host.validation}")
	private String twitchHost;

	@Value("${oauth.token.url}")
	private String accessTokenEndpoint;

	@Value("${twitch.client.id}")
	private String clientId;

	@Value("${twitch.client.secret}")
	private String clientSecret;

	@NonNull
	private final RestTemplate restTemplate;

	private OAuthResponse oauthResponse;
	private LocalDateTime lastCallTime;

	private final Supplier<String> supplier = Suppliers.synchronizedSupplier(() -> {
		// Checks to see if the token needs updated before returning a cached token from a previous call
		if (oauthResponse == null || lastCallTime.plusSeconds(oauthResponse.getExpiresIn()).isBefore(LocalDateTime.now())) {
			fetchOAuthToken();
		}

		return oauthResponse.getAccessToken();
	});

	/**
	 * Returns a cached (always will be valid) OAuth access_token or fetches a new access token
	 * from the Twitch API.
	 * @return String access token
	 */
	public String getAppAccessToken() {
		return supplier.get();
	}

	/**
	 * Updates an OAuth access token by making an Http POST to Twitch's authentication API's.
	 */
	private void fetchOAuthToken() {
		log.info("Attempting to make POST to {}{} to create fetch new application OAuth 2.0 Token.", twitchHost, accessTokenEndpoint);
		Map<String, String> params = new HashMap<>();
		params.put("client_id", clientId);
		params.put("client_secret", clientSecret);
		params.put("grant_type", "client_credentials");

		try {
			oauthResponse = restTemplate.postForObject(twitchHost + accessTokenEndpoint, params, OAuthResponse.class);
			lastCallTime = LocalDateTime.now();
			log.info("OAuth access_token fetched successfully. Response = {}", oauthResponse);
		} catch(Exception e) {
			log.error("There was an error while trying to fetch a new access_token from URL: {}{}.", twitchHost, accessTokenEndpoint, e);
		}
	}
}
