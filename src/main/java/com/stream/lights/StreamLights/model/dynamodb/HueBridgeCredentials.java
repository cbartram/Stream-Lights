package com.stream.lights.StreamLights.model.dynamodb;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@DynamoDbBean
public class HueBridgeCredentials {

	private String partitionKey;
	private String sortId;
	private String hueApiKey;
	private String accessToken;
	private String refreshToken;

	@DynamoDbPartitionKey
	public String getPartitionKey() {
		return this.partitionKey;
	}

	@DynamoDbSortKey
	public String getSortId() {
		return this.sortId;
	}
}
