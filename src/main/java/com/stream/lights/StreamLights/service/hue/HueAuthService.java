package com.stream.lights.StreamLights.service.hue;


import com.stream.lights.StreamLights.model.http.auth.OAuthResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class HueAuthService {

	@Value("${hue.host}")
	private String hueHost;

	@Value("${oauth.token.url}")
	private String accessTokenEndpoint;

	@Value("${hue.client.id}")
	private String clientId;

	@Value("${hue.client.secret}")
	private String clientSecret;

	@NonNull
	private final RestTemplate restTemplate;

	private OAuthResponse oauthResponse;
	private LocalDateTime lastCallTime;

	/**
	 * Returns a cached (always will be valid) OAuth access_token or fetches a new access token
	 * from the Twitch API.
	 * @return String access token
	 */
	public String getAppAccessToken(final String authorizationCode) {
		if (oauthResponse == null || lastCallTime.plusSeconds(oauthResponse.getExpiresIn()).isBefore(LocalDateTime.now())) {
			fetchOAuthToken(authorizationCode);
		}

		return oauthResponse.getAccessToken();
	}

	/**
	 * Updates an OAuth access token by making an Http POST to Twitch's authentication API's.
	 */
	private void fetchOAuthToken(final String authorizationCode) {
		log.info("Attempting to make POST to {}{} to create fetch new application OAuth 2.0 Token.", hueHost, accessTokenEndpoint);
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(clientId, clientSecret);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(hueHost + accessTokenEndpoint)
				.queryParam("code", authorizationCode)
				.queryParam("grant_type", "authorization_code");

		HttpEntity<?> entity = new HttpEntity<>(headers);
		try {
			HttpEntity<OAuthResponse> response = restTemplate.exchange(
					builder.toUriString(),
					HttpMethod.POST,
					entity,
					OAuthResponse.class);
			oauthResponse = response.getBody();
			lastCallTime = LocalDateTime.now();
			log.info("OAuth access_token fetched successfully. Response = {}", oauthResponse);
		} catch(Exception e) {
			log.error("There was an error while trying to fetch a new access_token from URL: {}{}.", hueHost, accessTokenEndpoint, e);
		}
	}
}
