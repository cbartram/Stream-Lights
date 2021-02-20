package com.stream.lights.StreamLights.controller;


import com.stream.lights.StreamLights.model.dynamodb.HueBridgeCredentials;
import com.stream.lights.StreamLights.service.hue.HueService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

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
	private HueService hueService;

	@NonNull
	private DynamoDbTable<HueBridgeCredentials> table;

	@GetMapping("/oauth2/callback")
	public ResponseEntity<HueBridgeCredentials> oauthCallback(@RequestParam final String code, @RequestParam(required = false) final String state) {
		log.info("Received authorization code: {} for user: {}", code, state);
		// Presses the virtual "link button" on users hue bridge and
		// creates a new user on their bridge
		final HueBridgeCredentials bridge = hueService.linkBridge(code);
		bridge.setPartitionKey(state);
		bridge.setSortId("#sort_id");
		log.info("Successfully Created Link: {} for the remote Hue bridge.", bridge);
		table.putItem(bridge);
		return ResponseEntity.ok(bridge);
	}


	// TODO this whole method will ultimately be replaced with a frontend which directly links the user to this page.
	@GetMapping("/oauth2/authorize")
	public String authorize(@RequestParam final String twitchUsername) {
		// This would be done on some frontend eventually
		//  MUST BE THE TWITCH USERNAME This is the only way to link an event from twitch to the OAuth token / api key in our database
		final String url = "https://api.meethue.com/oauth2/auth?clientid=" + clientId + "&response_type=code&state=" + twitchUsername + "&appid=stream-lights&deviceid=SpringBoot&devicename=SpringBoot_StreamLights";
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
