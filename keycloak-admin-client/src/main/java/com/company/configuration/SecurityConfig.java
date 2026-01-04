package com.company.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers(
                                        "/keycloak-admin-client/v3/api-docs/**",
                                        "/keycloak-admin-client/swagger-ui/**",
                                        "/keycloak-admin-client/swagger-ui.html"
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(ex -> ex.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
