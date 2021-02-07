package com.stream.lights.StreamLights.service.hue;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stream.lights.StreamLights.model.http.hue.HueLight;
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
	private String bridgeUrl;
	private ObjectMapper mapper;
	private RestTemplate restTemplate;

	public HueService(@Autowired RestTemplate restTemplate,
					  @Autowired ObjectMapper mapper,
					  @Value("${hue.url.lights}") String lights,
					  @Value("${hue.api.key}") String apiKey) {
		this.restTemplate = restTemplate;
		this.mapper = mapper;
		this.lightsUrl = lights.replace("{api_key}", apiKey);
		log.info("Using light API url: {}", this.lightsUrl);
		this.bridgeUrl = this.getBridgeUrl();
		this.getLights();
	}

	/**
	 * Uses the philips hue discovery service to fetch the bridge URL for the hue bridge on the local network.
	 * This is used to make future API calls to the hue bridge.
	 * @return String the full URL to the philips hue bridge on the network if it is found. Returns "unknown" if there
	 * is no bridge or it cannot be located.
	 */
	public String getBridgeUrl() {
		log.info("Attempting to make GET to https://discovery.meethue.com/ to find hue bridge URL.");
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = this.restTemplate.exchange("https://discovery.meethue.com/", HttpMethod.GET, entity, String.class);
		log.info("Hue Discovery service response status code = {} and body = {}", response.getStatusCode(), response.getBody());
		try {
			List<Map<String, String>> discoveredBridges = this.mapper.readValue(response.getBody(), new TypeReference<>() {});
			if(!discoveredBridges.isEmpty()) {
				log.info("Found: {} Hue bridges. Setting Hue bridge URL to: http://{}", discoveredBridges.size(), discoveredBridges.get(0).get("internalipaddress"));
				return "http://" + discoveredBridges.get(0).get("internalipaddress");
			} else {
				log.warn("No philips hue bridges detected on the local network. Make sure it is plugged in and connected to the same Wifi network as this server.");
				return "unknown";
			}
		} catch (JsonProcessingException e) {
			log.error("Failed to map Hue discovery response json to Java List.class JSON = {}", response.getBody(), e);
			return "unknown";
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
			List<HueLight> list = lightsMap.keySet().stream().map(lightsMap::get).collect(Collectors.toList());
			log.info("Mapped lights to list -> {}", list);
			return list;
		} catch (JsonProcessingException e) {
			log.error("Failed to map Hue discovery response json to Java List.class JSON = {}", response.getBody(), e);
			return Collections.emptyList();
		}
	}
}
