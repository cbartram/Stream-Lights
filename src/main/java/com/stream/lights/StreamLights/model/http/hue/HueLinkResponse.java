package com.stream.lights.StreamLights.model.http.hue;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * HueLinkResponse - Pojo representing the response from a successful link between a remote hue bridge and
 * this server. The response will contain a username (hue api key) which can be used to modify and get
 * access to a remote hue bridge.
 */
@Data
@Slf4j
public class HueLinkResponse {
	private Map<String, String> success;

	public String getUsername() {
		if(success != null) {
			return success.get("username");
		} else {
			log.error("Cannot retrieve key: username from a null map. The response from the hue bridge link process" +
					"may not have been deserialized correctly. The map \"success\" is null.");
			return null;
		}
	}
}
