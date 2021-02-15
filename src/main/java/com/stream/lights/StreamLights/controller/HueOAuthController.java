package com.stream.lights.StreamLights.controller;


import com.stream.lights.StreamLights.service.hue.HueAuthService;
import com.stream.lights.StreamLights.service.hue.HueService;
import com.stream.lights.StreamLights.util.Util;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HueOAuthController {

	@Value("${hue.client.id}")
	private String clientId;

	@NonNull
	private HueAuthService hueAuthService;

	@NonNull
	private HueService hueService;

	@GetMapping("/oauth2/callback")
	private String oauthCallback(@RequestParam final String code, @RequestParam(required = false) final String state) {
		log.info("Got access code: {} for state: {}", code, state);

		// 1.) Press virtual "link button" on users hue bridge
		//	https://api.meethue.com/bridge/0/config PUT { “linkbutton”:true }

		// 2.) Create a new user on their bridge
		// https://api.meethue.com/bridge/ POST { “devicetype”:”<your-application-name>” }

		final String createdUsername = hueService.linkBridge(code);

		// 3.) Good to make API calls
		//  GET https://api.meethue.com/bridge/<whitelist_identifier>/lights

		return "success";
	}

	@GetMapping("/oauth2/authorize")
	private String authorize() {
		// This would be done on some frontend eventually
		final String url = "https://api.meethue.com/oauth2/auth?clientid=" + clientId + "&response_type=code&state=" + Util.randomString(5) + "&appid=stream-lights&deviceid=SpringBoot&devicename=SpringBoot_StreamLights";
		if(Desktop.isDesktopSupported()){
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			return url;
		}

		return "success";
	}
}
