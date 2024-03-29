package com.danielbukowski.photosharing.Config;

import com.danielbukowski.photosharing.Handler.AuthenticationEntryPointHandler;
import com.danielbukowski.photosharing.Handler.AuthorizationDeniedHandler;
import com.danielbukowski.photosharing.Service.UserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.*;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPointHandler authenticationEntryPointHandler;
    private final AuthorizationDeniedHandler authorizationDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(POST, "/api/v3/accounts").permitAll()
                        .requestMatchers(GET, "/api/v2/images/**").permitAll()
                        .requestMatchers(POST, "/api/v3/accounts/email-verification").permitAll()
                        .requestMatchers(POST, "/api/v3/accounts/password-reset").permitAll()
                        .requestMatchers(PUT, "/api/v3/accounts/password-reset/**").permitAll()
                        .requestMatchers(GET, "/api/v1/csrf").permitAll()
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf ->
                        csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .sessionManagement(session -> {
                    session.maximumSessions(1).maxSessionsPreventsLogin(false);
                    session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
                    session.sessionFixation().changeSessionId();
                })
                .authenticationProvider(authProvider())
                .cors(Customizer.withDefaults())
                .httpBasic(h -> h.authenticationEntryPoint(authenticationEntryPointHandler))
                .exceptionHandling(eH -> eH.accessDeniedHandler(authorizationDeniedHandler))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
//        for local development
        config.addAllowedOrigin("http://localhost:4200");
//        for environment built in docker
        config.addAllowedOrigin("http://localhost");
        config.setAllowedHeaders(asList(
                AUTHORIZATION,
                CONTENT_TYPE,
                ACCEPT,
                "X-XSRF-TOKEN")
        );

        config.setAllowedMethods(asList(
                GET.name(),
                PATCH.name(),
                POST.name(),
                PUT.name(),
                OPTIONS.name(),
                DELETE.name()));
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
