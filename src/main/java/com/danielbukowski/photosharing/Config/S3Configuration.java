package com.danielbukowski.photosharing.Config;


import com.danielbukowski.photosharing.Property.S3Properties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;


@Configuration
@AllArgsConstructor
public class S3Configuration {
    private final S3Properties s3Properties;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCredits = AwsBasicCredentials.create(
                s3Properties.getAccessKey(),
                s3Properties.getSecretKey()
        );

        return S3Client.builder()
                .endpointOverride(URI.create(s3Properties.getUrl()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredits))
                .region(Region.EU_CENTRAL_1)
                .build();
    }

}
