package com.stream.lights.StreamLights.service.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OAuthProviderTest {

    @Test
    void oauthProvider_getSetValues_success() {
        OAuthProvider twitch = OAuthProvider.TWITCH;
        OAuthProvider hue = OAuthProvider.PHILLIPS_HUE;
        assertEquals("TWITCH", twitch.name());
        assertEquals("PHILLIPS_HUE", hue.name());
    }
}
