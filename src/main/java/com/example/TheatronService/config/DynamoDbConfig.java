package com.example.TheatronService.config;

import com.example.TheatronService.model.TheatronUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.awscore.endpoints.AccountIdEndpointMode;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

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
        System.setProperty("aws.accessKeyId", "abcd124321sa");
        System.setProperty("aws.secretAccessKey", "super-secret-key");
        return DynamoDbClient
                .builder()
                .region(Region.of("LOCAL"))
                .endpointOverride(URI.create("http://localhost:8000"))
                .build();
    }

}
