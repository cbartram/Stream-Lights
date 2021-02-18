package com.stream.lights.StreamLights.service.auth;

/**
 * GrantType - Enum value which specifies the grant type to use
 * to authenticate and retrieve an access token.
 */
public enum GrantType {
	CLIENT_CREDENTIALS,
	AUTHORIZATION_CODE,
	REFRESH_TOKEN
}
