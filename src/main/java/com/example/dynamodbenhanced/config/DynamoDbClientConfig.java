package com.example.dynamodbenhanced.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DynamoDbClientConfig {

    @Bean
    @Primary
    @Profile({"local-beta", "beta", "stage", "prod"})
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .overrideConfiguration(createClientConfiguration())
                .region(Region.AP_NORTHEAST_2)
                .build();
    }

    private static ClientOverrideConfiguration createClientConfiguration() {
        return ClientOverrideConfiguration.builder()
                .retryPolicy(RetryPolicy.none())
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    @Primary
    @Profile({"test", "local"})
    public DynamoDbClient testDynamoDbClient(EmbeddedDynamoDbConfig embededDynamoConfig) throws URISyntaxException {
        return DynamoDbClient.builder()
                .overrideConfiguration(createClientConfiguration())
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local")))
                .endpointOverride(new URI("http://localhost:" + embededDynamoConfig.getPort()))
                .region(Region.AP_NORTHEAST_2)
                .build();
    }
}