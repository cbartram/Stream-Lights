package com.stream.lights.StreamLights;

import com.stream.lights.StreamLights.model.dynamodb.HueBridgeCredentials;
import com.stream.lights.StreamLights.model.http.twitch.TwitchEvent;
import com.stream.lights.StreamLights.model.http.twitch.TwitchSubRequest;
import com.stream.lights.StreamLights.model.http.twitch.TwitchSubRequest.SubscriptionCondition;
import com.stream.lights.StreamLights.model.http.twitch.TwitchSubRequest.SubscriptionTransport;
import com.stream.lights.StreamLights.model.http.twitch.TwitchWebhookRequest;
import com.stream.lights.StreamLights.model.http.twitch.TwitchWebhookRequest.WebhookSubscription;

import java.time.LocalDateTime;

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

	public static final String TWITCH_LIST_SUBSCRIPTION_JSON_RESPONSE = "{\n" +
			"    \"total\": 1,\n" +
			"    \"data\": [\n" +
			"        {\n" +
			"            \"id\": \"9e71-2569-4902-a906-a85b\",\n" +
			"            \"status\": \"enabled\",\n" +
			"            \"type\": \"channel.follow\",\n" +
			"            \"version\": \"1\",\n" +
			"            \"condition\": {\n" +
			"                \"broadcaster_user_id\": \"106032\"\n" +
			"            },\n" +
			"            \"created_at\": \"2021-02-07T17:38:20.799440479Z\",\n" +
			"            \"transport\": {\n" +
			"                \"method\": \"webhook\",\n" +
			"                \"callback\": \"https://5c6749b7.ngrok.io/twitch/subscription/webhook\"\n" +
			"            }\n" +
			"        }\n" +
			"    ],\n" +
			"    \"limit\": 10000,\n" +
			"    \"pagination\": {}\n" +
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

	public static HueBridgeCredentials initHueCredentials() {
		final HueBridgeCredentials credentials = new HueBridgeCredentials();
		credentials.setRefreshToken("refresh");
		credentials.setAccessToken("access");
		credentials.setPartitionKey("partition");
		credentials.setSortId("sort");
		credentials.setHueApiKey("api");
		return credentials;
	}

	public static TwitchEvent initTwitchEvent() {
		final TwitchEvent event = new TwitchEvent();
		event.setBroadcasterUserId("broadcast_user_id");
		event.setBroadcasterUsername("broadcast_username");
		event.setUserId("userId");
		event.setUserLogin("login");
		event.setUsername("username");
		return event;
	}

	public static TwitchWebhookRequest initTwitchWebhookRequest() {
		final TwitchWebhookRequest request = new TwitchWebhookRequest();
		final WebhookSubscription subscription = new WebhookSubscription();
		final SubscriptionCondition condition = new SubscriptionCondition();
		final SubscriptionTransport transport = new SubscriptionTransport();

		condition.setBroadcasterUserId("buid");
		condition.setUsername("username");

		transport.setMethod("method");
		transport.setSecret("secret");
		transport.setCallback("callback");

		subscription.setType("type");
		subscription.setVersion("version");
		subscription.setCreatedAt(LocalDateTime.MAX);
		subscription.setId("id");
		subscription.setStatus("status");
		subscription.setCondition(condition);
		subscription.setTransport(transport);

		request.setEvent(null);
		request.setSubscription(subscription);
		request.setChallenge("challenge_id");

		return request;
	}
}
