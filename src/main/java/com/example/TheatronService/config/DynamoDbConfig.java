package com.example.TheatronService.config;

import com.example.TheatronService.model.TheatronUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.endpoints.AccountIdEndpointMode;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    @Value("${aws.dynamodb.accessKey}")
    private String ACCESS_KEY;

    @Value("${aws.dynamodb.secretKey}")
    private String SECRET_KEY;

    @Value("${aws.dynamodb.endpoint}")
    private String DYNAMO_ENDPOINT;

    @Bean
    DynamoDbClient dynamoDbClient() {
        return getDynamoDbClient();
    }

    @Bean
    DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient
                .builder()
                .dynamoDbClient(getDynamoDbClient())
                .build();
    }

    private DynamoDbClient getDynamoDbClient() {
        return DynamoDbClient
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(DYNAMO_ENDPOINT))
                .build();
    }

}
