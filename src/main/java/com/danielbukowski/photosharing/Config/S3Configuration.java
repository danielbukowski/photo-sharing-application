package com.danielbukowski.photosharing.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;


@Configuration
public class S3Configuration {

    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String secretKey;
    @Value("${AWS_ACCESS_KEY_ID}")
    private String accessKey;
    @Value("${AWS_ENDPOINT_URL}")
    private String endpoint;
    @Value("${AWS_REGION}")
    private String region;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCredits = AwsBasicCredentials.create(
                accessKey,
                secretKey
        );

        return S3Client.builder()
                .forcePathStyle(true)
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredits))
                .region(Region.of(region))
                .build();
    }

}
