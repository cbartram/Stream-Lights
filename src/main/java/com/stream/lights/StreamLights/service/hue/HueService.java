package com.stream.lights.StreamLights.service.hue;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stream.lights.StreamLights.model.http.hue.HueLight;
import com.stream.lights.StreamLights.model.http.hue.HueLinkResponse;
import com.stream.lights.StreamLights.service.auth.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HueService - Service class which wraps around the philips Hue bridge API to provide
 * convenient methods to link a remote bridge, query for lights on the network, and modify
 * light state (on/off and color).
 */
@Slf4j
@Service
public class HueService {

	private String hueHost;
	private String lightsUrl;
	private String lightsStateUrl;
	private String bridgeConfigUrl;

	private ObjectMapper mapper;
	private RestTemplate restTemplate;
	private OAuthService oauthService;

	public HueService(@Autowired final RestTemplate restTemplate,
					  @Autowired final OAuthService oauthService,
					  @Autowired final ObjectMapper mapper,
					  @Value("${hue.host}") final String hueHost,
					  @Value("${hue.url.lights}") final String lightsUrl,
					  @Value("${hue.url.bridge.config}") final String bridgeConfigUrl,
					  @Value("${hue.url.lights.state}") final String lightsStateUrl
	) {
		this.restTemplate = restTemplate;
		this.mapper = mapper;
		this.hueHost = hueHost;
		this.lightsUrl = lightsUrl;
		this.lightsStateUrl = lightsStateUrl;
		this.bridgeConfigUrl = bridgeConfigUrl;
		this.oauthService = oauthService;
	}

	/**
	 * Creates a link on a remote Hue bridge through OAuth 2. This method returns a String representing the API key
	 * which can be used to modify or query lights on a remote Hue bridge.
	 * @param authorizationCode String The OAuth authorization code from the users approving access for stream-lights
	 *                          to gather their personal information on the hue bridge.
	 * @return String the Api key used in subsequent requests to the bridge.
	 */
	public String linkBridge(final String authorizationCode) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(this.oauthService.fetchHueAccessToken(authorizationCode).getToken());
		HttpEntity<String> entity = new HttpEntity<>("{\"linkbutton\": true}", headers);
		ResponseEntity<String> response = restTemplate.exchange(this.hueHost + bridgeConfigUrl, HttpMethod.PUT, entity, String.class);
		log.info("Response from remote bridge linking API call to {} is {}", this.hueHost + bridgeConfigUrl, response); // TODO change to debug when done if this contains useful information about the bridge we could use this
		// TODO as a cache key
		if(response.getStatusCode().is2xxSuccessful()) {
			log.info("Successfully linked with remote Hue Bridge. Attempting to create new Api Key with remote bridge.");
			// Now that we have pressed the virtual "link" button on the users bridge we need to quickly create a new API key.
			HttpEntity<String> createApiKeyRequestBody = new HttpEntity<>("{\"devicetype\":\"stream-lights\"}", headers);
			ResponseEntity<String> createApiKeyResponse = restTemplate.exchange(
					this.hueHost + "/bridge",
					HttpMethod.POST,
					createApiKeyRequestBody,
					String.class);
			log.debug("Create remote bridge Api key response: {}", createApiKeyResponse);

			if(createApiKeyResponse.getStatusCode().is2xxSuccessful()) {
				try {
					List<HueLinkResponse> createdKeys = mapper.readValue(createApiKeyResponse.getBody(), new TypeReference<>() {});
					return createdKeys.get(0).getApiKey();
				} catch (JsonProcessingException e) {
					log.error("Failed to map json response from hue link API call back to list of HueLinkResponse objects. ", e);
					return null;
				}
			}
			log.error("Error creating new API key on linked remote Hue bridge. Status Code = {} and body = {}", createApiKeyResponse.getStatusCode(), createApiKeyResponse.getBody());
			return null;
		}
		log.error("Error creating remote link to Hue Bridge. Cannot PUT to: {}, Status = {} Body = {}", this.hueHost + bridgeConfigUrl, response.getStatusCode(), response.getBody());
		return null;
	}

	/**
	 * Returns a list of Light objects representing all the Philips hue lights found on the network.
	 * Each light object can be individually controlled.
	 * @return List of Light objects.
	 */
	public List<HueLight> getLights(final String apiKey) {
		final String getLightsEndpoint = this.lightsUrl.replace("{api_key}", apiKey);
		log.info("Attempting to make GET to {}{} to find Hue lights on the network.", this.hueHost, getLightsEndpoint);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = this.restTemplate.exchange(this.hueHost + getLightsEndpoint, HttpMethod.GET, entity, String.class);
		log.debug("Hue lights response status code = {} and body = {}", response.getStatusCode(), response.getBody());
		if(response.getStatusCode().is2xxSuccessful()) {
			try {
				Map<String, HueLight> lightsMap = this.mapper.readValue(response.getBody(), new TypeReference<>() {
				});
				List<HueLight> list = lightsMap.keySet().stream().map(key -> {
					HueLight l = lightsMap.get(key);
					l.setLightId(key);
					return l;
				}).collect(Collectors.toList());
				log.info("Mapped lights to list -> {}", list);
				return list;
			} catch (JsonProcessingException e) {
				log.error("Failed to map Hue discovery response json to Java List.class JSON = {}", response.getBody(), e);
				return Collections.emptyList();
			}
		}

		log.error("Failed to fetch list of lights on the network. Status code = {} and response = {}", response.getStatusCode(), response.getBody());
		return Collections.emptyList();
	}

	/**
	 * Turns the specified light on.
	 * @param lightId String light id of the light to change the state for
	 * @param apiKey String the API key to authenticate with the correct Hue remote bridge
	 */
	public void on(final String lightId, final String apiKey) {
		putState(lightId, apiKey, "{ \"on\": true }");
	}

	/**
	 * Turns the specified light off.
	 * @param lightId String light id of the light to change the state for
	 * @param apiKey String the API key to authenticate with the correct Hue remote bridge
	 */
	public void off(final String lightId, final String apiKey) {
		this.putState(lightId, apiKey, "{ \"on\": false }");
	}

	/**
	 * Updates the current state of a light to turn it on, off, or change its color.
	 * @param lightId String light id of the light to change the state for
	 * @param apiKey String the API key to authenticate with the correct Hue remote bridge
	 * @param state String the new JSON state to PUT for the light.
	 */
	private void putState(final String lightId, final String apiKey, final String state) {
		final String lightStateEndpoint = this.lightsStateUrl
				.replace("{api_key}", apiKey)
				.replace("{light_id}", lightId);

		log.info("Attempting to make PUT to {} to change light to state ON", this.hueHost + lightStateEndpoint);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(state, headers);
		ResponseEntity<String> response = this.restTemplate.exchange(this.hueHost + lightStateEndpoint, HttpMethod.PUT, entity, String.class);
		log.debug("Hue lights put state status code = {} and body = {}", response.getStatusCode(), response.getBody());

		if(!response.getStatusCode().is2xxSuccessful())
		log.error("Something went wrong while trying to update light state information for light: {} using Api key = {}. Status Code = {} response = {}", lightId, apiKey, response.getStatusCode(), response.getBody());
	}
}
