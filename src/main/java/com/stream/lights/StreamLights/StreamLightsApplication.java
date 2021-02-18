package com.stream.lights.StreamLights;

import com.stream.lights.StreamLights.model.dynamodb.HueBridge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.time.Duration;

@SpringBootApplication
public class StreamLightsApplication {

	@Value("${http.connect.timeout}")
	private int connectTimeout;

	@Value("${http.read.timeout}")
	private int readTimeout;

	@Value("${aws.dynamodb.region}")
	private String region;

	@Value("${aws.dynamodb.table.name}")
	private String tableName;

	public static void main(String[] args) {
		SpringApplication.run(StreamLightsApplication.class, args);
	}

	/**
	 * Creates a re-usable DynamoDbClient object which can be used
	 * to query and put information into the DynamoDb database.
	 * @return DynamoDbEnhancedClient The enhanced dynamodb client.
	 */
	@Bean
	public DynamoDbEnhancedClient createDynamoDbClient() {
		return DynamoDbEnhancedClient.builder()
						.dynamoDbClient(DynamoDbClient.builder()
								.region(Region.of(region))
								.credentialsProvider(ProfileCredentialsProvider.create())
							.build())
				.build();
	}

	@Bean
	@DependsOn("createDynamoDbClient")
	public DynamoDbTable<HueBridge> createHueBridgeTable(@Autowired final DynamoDbEnhancedClient client) {
		return client.table(tableName, TableSchema.fromBean(HueBridge.class));
	}

	/**
	 * Constructs a customized RestTemplate for making http calls to external services
	 * @param builder RestTemplateBuilder builder object to create and modify properties
	 *                of the rest template.
	 * @return RestTemplate fully constructed rest template object.
	 */
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
				.setConnectTimeout(Duration.ofMillis(connectTimeout))
				.setReadTimeout(Duration.ofMillis(readTimeout))
				.build();
	}
}
