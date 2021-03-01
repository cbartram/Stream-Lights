package com.stream.lights.StreamLights.service.auth;

import com.stream.lights.StreamLights.model.http.auth.OAuthResponse;
import com.stream.lights.StreamLights.util.Util;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

	@Value("${twitch.host.validation}")
	private String twitchAuthHost;

	@Value("${twitch.client.id}")
	private String twitchClientId;

	@Value("${twitch.client.secret}")
	private String twitchClientSecret;

	@Value("${hue.host}")
	private String hueHost;

	@Value("${hue.client.id}")
	private String hueClientId;

	@Value("${hue.client.secret}")
	private String hueClientSecret;

	@NonNull
	private RestTemplate restTemplate;

	private final Map<String, OAuthResponse> cache = new HashMap<>();

	/**
	 * Checks the cache for a valid Twitch OAuth token and if none is found fetches a new Twitch OAuth token
	 * using the client credentials.
	 * @return OAuthResponse Object which contains meta-data and an OAuth access_token
	 */
	public OAuthResponse fetchTwitchAccessToken() {
		final String key = "TWITCH.<user_id>";
		if(cache.containsKey(key)) {
			log.info("Cache hit for Twitch OAuth access_token: {} on key = {}", Util.mask(cache.get(key).getToken(), 6), key);
			return cache.get(key);
		}

		return fetchAccessToken(twitchAuthHost, twitchClientId, twitchClientSecret, GrantType.CLIENT_CREDENTIALS, OAuthProvider.TWITCH, null, null);
	}

	/**
	 * Checks the cache for a valid Hue OAuth token and if none is found fetches a new Hue OAuth token
	 * using the client credentials.
	 * @return OAuthResponse Object which contains meta-data and an OAuth access_token
	 */
	public OAuthResponse fetchHueAccessToken(final String authorizationCode) {
		final String key = "PHILLIPS_HUE.<user_id>";
		if(cache.containsKey(key)) {
			log.info("Cache hit for Phillips Hue OAuth access_token: {} on key = {}", Util.mask(cache.get(key).getToken(), 6), key);
			return cache.get(key);
		}

		return fetchAccessToken(hueHost, hueClientId, hueClientSecret, GrantType.AUTHORIZATION_CODE, OAuthProvider.PHILLIPS_HUE, authorizationCode, null);
	}

	public OAuthResponse refreshHueAccessToken(final String refreshToken) {
		final String key = "PHILLIPS_HUE.<user_id>";
		if(cache.containsKey(key)) {
			log.info("Cache hit for Phillips Hue OAuth access_token: {} on key = {}", Util.mask(cache.get(key).getToken(), 6), key);
			return cache.get(key);
		}

		return fetchAccessToken(hueHost, hueClientId, hueClientSecret, GrantType.REFRESH_TOKEN, OAuthProvider.PHILLIPS_HUE, null, refreshToken);
	}

	/**
	 * Method which fetches an OAuth token from an OAuth provider. This method supports both the client credentials and
	 * authorization code OAuth grant types (flows) for either provider.
	 * @param host String the host to connect to in order to fetch the token (OAuth resource server host)
	 * @param clientId String the client id to use in the client credentials grant type
	 * @param clientSecret String the client secret to use in the client credentials grant type
	 * @param grantType GrantType enum which represents the grant type to use to authenticate
	 * @param provider OAuthProvider The oauth provider to use (Either twitch or phillips_hue)
	 * @param authorizationCode String the Authorization code to use in the authorization code grant type. This will
	 *                          be null in a client credentials grant type scenario.
	 * @return OAuthResponse Object which contains meta-data about an OAuth token as well as the access_token itself.
	 */
	public OAuthResponse fetchAccessToken(String host, final String clientId, final String clientSecret, final GrantType grantType, final OAuthProvider provider, String authorizationCode, String refreshToken) {
		log.info("Attempting to make POST to {}/oauth2/token to fetch new OAuth 2.0 Token using {} grant type", host, grantType);
		UriComponentsBuilder builder;
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> entity;
		headers.set("Authorization", Util.toBasicAuth(clientId, clientSecret));

		// Allows for unit tests with null values TODO this is temporary need to find a better solution
		if(host == null) {
			host = "http://testhost";
		}

		if(grantType == GrantType.AUTHORIZATION_CODE) {
			builder = UriComponentsBuilder.fromHttpUrl(host + "/oauth2/token");
			builder.queryParam("code", authorizationCode)
					.queryParam("grant_type", "authorization_code");
			entity = new HttpEntity<>(headers);
		} else if(grantType == GrantType.REFRESH_TOKEN) {
			builder = UriComponentsBuilder.fromHttpUrl(host + "/oauth2/refresh");
			builder.queryParam("grant_type", "refresh_token");
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("refresh_token", refreshToken);
			entity = new HttpEntity<>(map, headers);
		} else {
			builder = UriComponentsBuilder.fromHttpUrl(host + "/oauth2/token");
			builder.queryParam("client_id", clientId)
					.queryParam("client_secret", clientSecret)
					.queryParam("grant_type", "client_credentials");
			entity = new HttpEntity<>(headers);
		}

		log.info(builder.toUriString());
		ResponseEntity<OAuthResponse> response = restTemplate.exchange(
				builder.toUriString(),
				HttpMethod.POST,
				entity,
				OAuthResponse.class);

		if(response.getStatusCode().is2xxSuccessful()) {
			cache.put(provider.name() + ".<user_id>", response.getBody());
			log.debug("OAuth access_token fetched successfully. Response = {}", response.getBody());
			return response.getBody();
		}

		log.error("There was an error while trying to fetch a new access_token from URL: {}/oauth2/token. Status = {} Error = {}", host, response.getStatusCode(), response.getBody());
		return new OAuthResponse();
	}
}
