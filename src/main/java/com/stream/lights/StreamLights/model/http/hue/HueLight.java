package com.stream.lights.StreamLights.model.http.hue;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * HueLight - Pojo class representing all the data associated with a Philips hue lightbulb on the network. This is a
 * large object will many sub static classes representing individual maps of lightbulb meta-data.
 */
@Data
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class HueLight {

	// Custom property which comes from the map of the json response. This preserves the actual light id
	// which would be lost when the list is converted from a map to a list.
	@JsonIgnore
	private transient String lightId;

	@JsonIgnore
	private transient String bridgeUrl; // Includes the full bridge host + API key

	@JsonIgnore
	private transient RestTemplate restTemplate;

	private State state;

	@JsonProperty("swupdate")
	private Swupdate softwareUpdate;
	private String type;
	private String name;

	@JsonProperty("modelid")
	private String modelId;

	@JsonProperty("manufacturername")
	private String manufacturerName;

	@JsonProperty("productname")
	private String productName;

	@JsonProperty("uniqueid")
	private String uniqueId;

	@JsonProperty("swversion")
	private String softwareVersion;

	@JsonProperty("swconfigid")
	private String softwareConfigId;

	@JsonProperty("productid")
	private String productId;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class State {
		private boolean on;
		private int bri;
		private int hue;
		private int sat;
		private String effect;
		private List<Double> xy;
		private int ct;
		private String alert;

		@JsonProperty("colormode")
		private String colorMode;
		private String mode;
		private boolean reachable;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Swupdate {
		private String state;

		@JsonProperty("lastinstall")
		private LocalDateTime lastInstall;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Ct {
		private int min;
		private int max;
	}

	public void on() {
		log.info("Attempting to make PUT to {} to change light to state ON", this.bridgeUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>("{ \"on\": true }", headers);

		// TODO should this really have a rest template? Its a POJO
		ResponseEntity<String> response = this.restTemplate.exchange(this.bridgeUrl, HttpMethod.PUT, entity, String.class);
		log.info("Hue lights ON response status code = {} and body = {}", response.getStatusCode(), response.getBody());
	}

	public void off() {
		log.info("Attempting to make PUT to {} to change light to state OFF", this.bridgeUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>("{ \"on\": false }", headers);

		// TODO should this really have a rest template? Its a POJO
		ResponseEntity<String> response = this.restTemplate.exchange(this.bridgeUrl, HttpMethod.PUT, entity, String.class);
		log.info("Hue lights OFF response status code = {} and body = {}", response.getStatusCode(), response.getBody());
	}
}
