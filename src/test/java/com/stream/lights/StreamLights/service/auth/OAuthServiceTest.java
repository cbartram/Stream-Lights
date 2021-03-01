package com.stream.lights.StreamLights.service.auth;

import com.stream.lights.StreamLights.TestUtils;
import com.stream.lights.StreamLights.model.http.auth.OAuthResponse;
import com.stream.lights.StreamLights.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@TestPropertySource(locations = "classpath:application-test.properties")
public class OAuthServiceTest {

    @InjectMocks
    private OAuthService oAuthService;

    @Mock
    private RestTemplate restTemplate;

    @Value("${hue.client.id}")
    private String hueClientId;

    @Value("${hue.client.secret}")
    private String hueClientSecret;

    @Test
    void oauthService_fetchToken_authorizationCodeSuccess() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("client_id", "client_secret");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        when(restTemplate.exchange("http://testhost/oauth2/token?code=abc123&grant_type=authorization_code", HttpMethod.POST, entity, OAuthResponse.class))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .body(TestUtils.initOAuthResponse())
                );

        OAuthResponse response = oAuthService.fetchAccessToken("http://testhost", "client_id", "client_secret", GrantType.AUTHORIZATION_CODE, OAuthProvider.TWITCH, "abc123", "refresh_token");
        assertEquals("access_token", response.getToken());
        assertEquals("refresh_token", response.getRefreshToken());
        assertEquals(100, response.getExpiresIn());
        assertEquals(Collections.singletonList("read"), response.getScope());
        assertEquals("bearer", response.getTokenType());
    }

    @Test
    void oauthService_fetchToken_refreshTokenSuccess() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("client_id", "client_secret");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("refresh_token", "refresh_token");
        HttpEntity<?> entity = new HttpEntity<>(map, headers);

        when(restTemplate.exchange("http://testhost/oauth2/refresh?grant_type=refresh_token", HttpMethod.POST, entity, OAuthResponse.class))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .body(TestUtils.initOAuthResponse())
                );

        OAuthResponse response = oAuthService.fetchAccessToken("http://testhost", "client_id", "client_secret", GrantType.REFRESH_TOKEN, OAuthProvider.TWITCH, null, "refresh_token");
        assertEquals("access_token", response.getToken());
        assertEquals("refresh_token", response.getRefreshToken());
        assertEquals(100, response.getExpiresIn());
        assertEquals(Collections.singletonList("read"), response.getScope());
        assertEquals("bearer", response.getTokenType());
    }


    @Test
    void oauthService_fetchToken_clientCredentialsSuccess() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("client_id", "client_secret");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        when(restTemplate.exchange("http://testhost/oauth2/token?client_id=client_id&client_secret=client_secret&grant_type=client_credentials", HttpMethod.POST, entity, OAuthResponse.class))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .body(TestUtils.initOAuthResponse())
                );

        OAuthResponse response = oAuthService.fetchAccessToken("http://testhost", "client_id", "client_secret", GrantType.CLIENT_CREDENTIALS, OAuthProvider.TWITCH, null, null);
        assertEquals("access_token", response.getToken());
        assertEquals("refresh_token", response.getRefreshToken());
        assertEquals(100, response.getExpiresIn());
        assertEquals(Collections.singletonList("read"), response.getScope());
        assertEquals("bearer", response.getTokenType());
    }


    @Test
    void oauthService_fetchToken_failedReturnsEmptyObject() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("client_id", "client_secret");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        when(restTemplate.exchange("http://testhost/oauth2/token?client_id=client_id&client_secret=client_secret&grant_type=client_credentials", HttpMethod.POST, entity, OAuthResponse.class))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new OAuthResponse())
                );

        OAuthResponse response = oAuthService.fetchAccessToken("http://testhost", "client_id", "client_secret", GrantType.CLIENT_CREDENTIALS, OAuthProvider.TWITCH, null, null);
        assertEquals(new OAuthResponse(), response);
    }


    @Test
    void oauthService_refreshHueAccessToken_success() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", Util.toBasicAuth("username", "password"));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("refresh_token", "refresh_token");
        HttpEntity<?> entity = new HttpEntity<>(map, headers);

        when(restTemplate.exchange("http://testhost/oauth2/refresh?grant_type=refresh_token", HttpMethod.POST, entity, OAuthResponse.class))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .body(TestUtils.initOAuthResponse())
                );

        OAuthResponse response = oAuthService.refreshHueAccessToken("refresh_token");
        assertEquals("access_token", response.getToken());
        assertEquals("refresh_token", response.getRefreshToken());
        assertEquals(100, response.getExpiresIn());
        assertEquals(Collections.singletonList("read"), response.getScope());
        assertEquals("bearer", response.getTokenType());
    }
}
