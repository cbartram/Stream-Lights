package com.stream.lights.StreamLights.model.http.twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * CreateSubscriptionRequest - POJO model which will be serialized into a JSON body for creating a new
 * twitch subscription.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateSubscriptionRequest {
	private String type;
	private String version;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SubscriptionCondition {

		@JsonProperty("broadcaster_user_id")
		private String broadcasterUserId;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SubscriptionTransport {
		private String method;
		private String callback;
		private String secret;
	}
}
