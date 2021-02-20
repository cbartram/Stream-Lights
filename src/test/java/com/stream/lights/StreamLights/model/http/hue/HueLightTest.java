package com.stream.lights.StreamLights.model.http.hue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HueLightTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String LIGHT_JSON = "{\n" +
            "    \"1\": {\n" +
            "      \"state\": {\n" +
            "        \"on\": true,\n" +
            "        \"bri\": 240,\n" +
            "        \"hue\": 15331,\n" +
            "        \"sat\": 121,\n" +
            "        \"xy\": [\n" +
            "          0.4448,\n" +
            "          0.4066\n" +
            "        ],\n" +
            "        \"ct\": 343,\n" +
            "        \"alert\": \"none\",\n" +
            "        \"effect\": \"none\",\n" +
            "        \"colormode\": \"ct\",\n" +
            "        \"reachable\": true\n" +
            "      },\n" +
            "      \"type\": \"Extended color light\",\n" +
            "      \"name\": \"TV\",\n" +
            "      \"modelid\": \"LCT001\",\n" +
            "      \"swversion\": \"65003148\",\n" +
            "      \"pointsymbol\": {\n" +
            "        \"1\": \"none\",\n" +
            "        \"2\": \"none\",\n" +
            "        \"3\": \"none\",\n" +
            "        \"4\": \"none\",\n" +
            "        \"5\": \"none\",\n" +
            "        \"6\": \"none\",\n" +
            "        \"7\": \"none\",\n" +
            "        \"8\": \"none\"\n" +
            "      }\n" +
            "    }\n" +
            "  }}";

    @Test
    void hueLight_getSetFields_success() throws JsonProcessingException {
        Map<String, HueLight> lightsMap = mapper.readValue(LIGHT_JSON, new TypeReference<>() {
        });
        List<HueLight> list = lightsMap.keySet().stream().map(key -> {
            HueLight l = lightsMap.get(key);
            l.setLightId(key);
            return l;
        }).collect(Collectors.toList());

        HueLight light = list.get(0);
        assertEquals(1, list.size());
        assertEquals("1", light.getLightId());
        assertTrue(light.getState().isOn());
        assertTrue(light.getState().isReachable());
        assertEquals(240, light.getState().getBri());
        assertEquals(15331, light.getState().getHue());
        assertEquals(121, light.getState().getSat());
        assertEquals(343, light.getState().getCt());
        assertEquals("ct", light.getState().getColorMode());
        assertEquals("none", light.getState().getAlert());
        assertEquals("none", light.getState().getEffect());
        assertEquals("Extended color light", light.getType());
        assertEquals("TV", light.getName());
        assertEquals("LCT001", light.getModelId());
    }
}
