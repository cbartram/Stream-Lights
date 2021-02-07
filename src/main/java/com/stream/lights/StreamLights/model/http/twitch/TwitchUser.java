package com.stream.lights.StreamLights.model.http.twitch;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * TwitchUser - Java POJO representing public details for a twitch user's profile.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchUser {
	private String id;
	private String login;

	@JsonProperty("display_name")
	private String displayName;

	private String type;

	@JsonProperty("broadcaster_type")
	private String broadcasterType;

	private String description;

	@JsonProperty("profile_image_url")
	private String profilePicture;

	@JsonProperty("offline_image_url")
	private String offlineProfilePicture;

	@JsonProperty("view_count")
	private int viewCount;

	@JsonProperty("created_at")
	private LocalDateTime createdAt;
}
