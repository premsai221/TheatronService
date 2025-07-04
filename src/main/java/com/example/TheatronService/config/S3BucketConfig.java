package com.example.TheatronService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.util.List;

@Configuration
public class S3BucketConfig {

    @Value("${aws.s3.accessKey}")
    private String ACCESS_KEY;

    @Value("${aws.s3.secretKey}")
    private String SECRET_KEY;

    @Value("${aws.s3.endpoint}")
    private String S3_ENDPOINT;

    @Bean
    public S3Client getS3Client() {
//        System.setProperty("aws.region", "us-east-1");
        return S3Client.builder()
                .endpointOverride(URI.create(S3_ENDPOINT))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)
                ))
                .forcePathStyle(true)
                .region(Region.US_EAST_1)
                .build();
    }

}
