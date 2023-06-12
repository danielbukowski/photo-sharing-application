package com.danielbukowski.photosharing.Property;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "s3")
public class S3Properties {
    private String url;
    private String bucketName;
    private String accessKey;
    private String secretKey;
}
