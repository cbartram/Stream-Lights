package com.stream.lights.StreamLights.model.http.twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * CreateSubscriptionRequest - POJO model which will be serialized into a JSON body for creating a new
 * twitch subscription.
 */
@Data
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchSubRequest {
	private String type;
	private String version;
	private SubscriptionCondition condition;
	private SubscriptionTransport transport;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SubscriptionCondition {

		@JsonProperty("broadcaster_user_id")
		private String broadcasterUserId;

		// Users supply this field to the API but it is NOT sent to twitch API's
		// therefore it is WRITE_ONLY and will NOT be serialized when this request body is sent to twitch.
		@JsonProperty(access = Access.WRITE_ONLY)
		private String username;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SubscriptionTransport {
		private String method;
		private String callback;
		private String secret;
	}
}
