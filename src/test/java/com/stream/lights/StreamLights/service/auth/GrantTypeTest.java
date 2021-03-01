package com.stream.lights.StreamLights.service.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GrantTypeTest {

    @Test
    void grantType_getSetValues_success() {
        GrantType authorizationCode = GrantType.AUTHORIZATION_CODE;
        GrantType clientCredentials = GrantType.CLIENT_CREDENTIALS;
        GrantType refresh = GrantType.REFRESH_TOKEN;
        assertEquals("AUTHORIZATION_CODE", authorizationCode.name());
        assertEquals("CLIENT_CREDENTIALS", clientCredentials.name());
        assertEquals("REFRESH_TOKEN", refresh.name());
    }
}
