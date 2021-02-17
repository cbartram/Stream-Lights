package com.stream.lights.StreamLights.model.http.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * OAuthResponse - Pojo model representing a standard response from an OAuth request
 * to generate a new access token.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthResponse {

	@Getter(AccessLevel.NONE)
	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("refresh_token")
	private String refreshToken;

	@JsonProperty("expires_in")
	private long expiresIn;

	private List<String> scope;

	@JsonProperty("token_type")
	private String tokenType;

	public String getToken() {
		return this.accessToken;
	}
}
