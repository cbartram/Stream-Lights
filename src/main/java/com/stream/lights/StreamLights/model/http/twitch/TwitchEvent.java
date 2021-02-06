package com.stream.lights.StreamLights.model.http.twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * TwitchEvent - Pojo class representing Twitch json sent to the webhook callback when an event is received for a
 * specific subscription.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchEvent {
	@JsonProperty("user_id")
	private String userId;

	@JsonProperty("user_login")
	private String userLogin;

	@JsonProperty("user_name")
	private String username;

	@JsonProperty("broadcaster_user_id")
	private String broadcasterUserId;

	@JsonProperty("broadcaster_user_login")
	private String broadcasterUserLogin;

	@JsonProperty("broadcaster_user_name")
	private String broadcasterUsername;
}
