package com.danielbukowski.photosharing.Property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "spring.encryption")
public class EncryptionProperties {

    private String password;
    private String salt;
}