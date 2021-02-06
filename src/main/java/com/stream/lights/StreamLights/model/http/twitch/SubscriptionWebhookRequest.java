package com.stream.lights.StreamLights.model.http.twitch;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * SubscriptionWebHookRequest - Pojo class which represents the payload sent from Twitch to the specified callback endpoint to
 * verify ownership of the callback.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionWebhookRequest {
	private String challenge;
	private WebhookSubscription subscription;


	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class WebhookSubscription {
		private String id;
		private String status;
		private String type;
		private String version;
		private CreateSubscriptionRequest.SubscriptionCondition condition;
		private CreateSubscriptionRequest.SubscriptionTransport transport;

		@JsonProperty("created_at")
		private LocalDateTime createdAt;
	}
}
