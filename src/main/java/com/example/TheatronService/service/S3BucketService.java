package com.example.TheatronService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.net.URL;
import java.time.Duration;

@Service
public class S3BucketService {
    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${aws.s3.endpoint}")
    private String s3Endpoint;

    @Value("${aws.s3.accessKey}")
    private String s3AccessKey;

    @Value("${aws.s3.secretKey}")
    private String s3SecretKey;

    private S3Client s3Client;

    public S3BucketService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String generatePreSignedUrl(String filename) {
        try (S3Presigner s3Presigner = S3Presigner.builder()
                .endpointOverride(URI.create(s3Endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(s3AccessKey, s3SecretKey)))
                .region(Region.US_EAST_1).build()) {
            String keyName = "uploads/".concat(filename);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();
            PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
                    .putObjectRequest(putObjectRequest)
                    .signatureDuration(Duration.ofMinutes(120))
                    .build();
            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(preSignRequest);
            System.out.println(presignedPutObjectRequest.url().toString());
            return presignedPutObjectRequest.url().toExternalForm();
        }

    }


    public String getS3UrlFromKey(String key) {
        URL s3Url = s3Client.utilities().getUrl(r -> r.bucket(bucketName).key(key));
        return s3Url.toExternalForm();
    }
}
