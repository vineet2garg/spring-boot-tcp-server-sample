package com.zhwxp.sample.spring.boot.tcp.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Configuration
public class DynamoDbConfig {

	@Value("${cloud.aws.credentials.access-key}")
	private String awsAccessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String awsSecretKey;

	@Value("${cloud.aws.region.static}")
	private String awsRegion;

	@Bean
	public DynamoDbAsyncClient dbAsyncClient() {
		DynamoDbAsyncClient dbAsyncClient = DynamoDbAsyncClient.builder().region(Region.of(awsRegion))
				.credentialsProvider(
						StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
				.build();
		return dbAsyncClient;
	}

}
