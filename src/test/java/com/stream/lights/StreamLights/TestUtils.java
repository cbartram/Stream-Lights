package com.stream.lights.StreamLights;

import com.stream.lights.StreamLights.model.http.twitch.TwitchSubRequest;
import com.stream.lights.StreamLights.model.http.twitch.TwitchSubRequest.SubscriptionCondition;
import com.stream.lights.StreamLights.model.http.twitch.TwitchSubRequest.SubscriptionTransport;

public class TestUtils {


	public static final String TWITCH_SUBSCRIPTION_SUCCESS_JSON_RESPONSE = "{\n" +
			"    \"data\": [\n" +
			"        {\n" +
			"            \"id\": \"9e5d4e71-2569-4902-a906-afa0aacde85b\",\n" +
			"            \"status\": \"webhook_callback_verification_pending\",\n" +
			"            \"type\": \"channel.follow\",\n" +
			"            \"version\": \"1\",\n" +
			"            \"condition\": {\n" +
			"                \"broadcaster_user_id\": \"106175032\"\n" +
			"            },\n" +
			"            \"created_at\": \"2021-02-07T17:38:20.799440479Z\",\n" +
			"            \"transport\": {\n" +
			"                \"method\": \"webhook\",\n" +
			"                \"callback\": \"https://5c67546749b7.ngrok.io/twitch/subscription/webhook\"\n" +
			"            }\n" +
			"        }\n" +
			"    ],\n" +
			"    \"limit\": 10000,\n" +
			"    \"total\": 1\n" +
			"}";

	public static SubscriptionCondition initSubscriptionCondition() {
		final SubscriptionCondition condition = new SubscriptionCondition();
		condition.setUsername("username");
		condition.setBroadcasterUserId("123456");
		return condition;
	}

	public static TwitchSubRequest initTwitchSubRequest() {
		final TwitchSubRequest request = new TwitchSubRequest();
		final SubscriptionCondition condition = new SubscriptionCondition();
		final SubscriptionTransport transport = new SubscriptionTransport();

		condition.setBroadcasterUserId("buid");
		condition.setUsername("username");

		transport.setMethod("method");
		transport.setSecret("secret");
		transport.setCallback("callback");

		request.setCondition(condition);
		request.setTransport(transport);
		request.setType("type");
		request.setVersion("version");

		return request;
	}
}
