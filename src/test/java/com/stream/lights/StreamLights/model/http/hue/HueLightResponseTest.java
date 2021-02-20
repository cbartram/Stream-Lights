package com.stream.lights.StreamLights.model.http.hue;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HueLightResponseTest {

    @Test
    void hueLinkResponse_getSetFields_success() {
        final HueLinkResponse response = new HueLinkResponse();
        Map<String, String> map = new HashMap<>();
        map.put("username", "foo");
        response.setSuccess(map);
        assertEquals("foo", response.getApiKey());
        assertEquals(map, response.getSuccess());
    }

    @Test
    void hueLinkResponse_getApiKey_nullWhenNoUsername() {
        final HueLinkResponse response = new HueLinkResponse();
        Map<String, String> map = new HashMap<>();
        map.put("no_username_field", "foo");
        response.setSuccess(map);
        assertNull(response.getApiKey());
        assertEquals(map, response.getSuccess());
    }
}
