package com.stream.lights.StreamLights.service.hue;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stream.lights.StreamLights.model.http.hue.HueLight;
import com.stream.lights.StreamLights.model.http.hue.HueLinkResponse;
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

@Slf4j
@Service
public class HueService {

	private String lightsUrl;
	private String lightsStateUrl;
	private ObjectMapper mapper;
	private RestTemplate restTemplate;
	private HueAuthService hueAuthService;
	private String hueHost;
	private String bridgeUrl;
	private String bridgeConfigUrl;

	public HueService(@Autowired RestTemplate restTemplate,
					  @Autowired HueAuthService hueAuthService,
					  @Autowired ObjectMapper mapper,
					  @Value("${hue.url.lights}") String lightsUrl,
					  @Value("${hue.host}") String hueHost,
					  @Value("${hue.url.bridge}") String bridgeUrl,
					  @Value("${hue.url.bridge.config}") String bridgeConfigUrl,
					  @Value("${hue.api.key}") String apiKey,
					  @Value("${hue.url.lights.state}") String lightsStateUrl
					  ) {
		this.restTemplate = restTemplate;
		this.mapper = mapper;
		this.lightsUrl = lightsUrl.replace("{api_key}", apiKey);
		this.lightsStateUrl = lightsStateUrl.replace("{api_key}", apiKey);
		this.hueHost = hueHost;
		this.bridgeUrl = bridgeUrl;
		this.bridgeConfigUrl = bridgeConfigUrl;
		this.hueAuthService = hueAuthService;
	}

	public String linkBridge(final String authorizationCode) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(hueAuthService.getAppAccessToken(authorizationCode));
		HttpEntity<String> entity = new HttpEntity<>("{\"linkbutton\": true}", headers);
		ResponseEntity<String> response = restTemplate.exchange(hueHost + bridgeUrl + bridgeConfigUrl, HttpMethod.PUT, entity, String.class);

		if(response.getStatusCode().is2xxSuccessful()) {
			// Now that we have pressed the virtual "link" button on the users bridge we need to quickly create a username.
			HttpEntity<String> createUsernameRequestBody = new HttpEntity<>("{\"devicetype\":\"stream-lights\"}", headers);
			ResponseEntity<String> createUsernameResponse = restTemplate.exchange(hueHost + bridgeUrl, HttpMethod.POST, createUsernameRequestBody, String.class);
			try {
				List<HueLinkResponse> createdUsernames = mapper.readValue(createUsernameResponse.getBody(), new TypeReference<>() {});
				log.info("Create username response: {}", createUsernameResponse);
				log.info("Successfully Created username: {}", createdUsernames.get(0).getUsername());
				return createdUsernames.get(0).getUsername();
			} catch(JsonProcessingException e) {
				log.error("Failed to map json response from hue link API call back to list of HueLinkResponse objects. ", e);
				return "error";
			}
		} else {
			log.error("Error creating remote link to Hue Bridge. Cannot PUT to: {}, Response Body = {}", hueHost + bridgeUrl + bridgeConfigUrl, response.getBody());
			return "error";
		}
	}

	/**
	 * Returns a list of Light objects representing all the Philips hue lights found on the network.
	 * @return List of Light objects.
	 */
	public List<HueLight> getLights() {
		log.info("Attempting to make GET to {}{} to find Hue lights on the network.", this.bridgeUrl, this.lightsUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = this.restTemplate.exchange(this.bridgeUrl + this.lightsUrl, HttpMethod.GET, entity, String.class);
		log.info("Hue lights response status code = {} and body = {}", response.getStatusCode(), response.getBody());
		try {
			Map<String, HueLight> lightsMap = this.mapper.readValue(response.getBody(), new TypeReference<>() {});
			List<HueLight> list = lightsMap.keySet().stream().map(key -> {
				HueLight l = lightsMap.get(key);
				l.setLightId(key);
				l.setBridgeUrl(this.bridgeUrl + this.lightsStateUrl.replace("{light_id}", key));
				l.setRestTemplate(this.restTemplate);
				return l;
			}).collect(Collectors.toList());
			log.info("Mapped lights to list -> {}", list);
			return list;
		} catch (JsonProcessingException e) {
			log.error("Failed to map Hue discovery response json to Java List.class JSON = {}", response.getBody(), e);
			return Collections.emptyList();
		}
	}
}
