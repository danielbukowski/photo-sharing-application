package com.danielbukowski.photosharing.Config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurationTest {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v3/accounts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v3/accounts/email-verification").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v3/accounts/password-reset").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v3/accounts/password-reset/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf().disable()
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
