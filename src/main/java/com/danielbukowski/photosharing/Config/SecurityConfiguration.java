package com.danielbukowski.photosharing.Config;

import com.danielbukowski.photosharing.Handler.AuthenticationEntryPointHandler;
import com.danielbukowski.photosharing.Handler.AuthorizationDeniedHandler;
import com.danielbukowski.photosharing.Service.UserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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
                        .requestMatchers(HttpMethod.POST, "/api/v3/accounts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v3/accounts/email-verification").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v3/accounts/password-reset").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v3/accounts/password-reset/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/csrf").permitAll()
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
                .sessionManagement(session -> {
                    session.maximumSessions(1).maxSessionsPreventsLogin(false);
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                    session.sessionFixation().newSession();
                })
                .authenticationProvider(authProvider())
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
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

}
