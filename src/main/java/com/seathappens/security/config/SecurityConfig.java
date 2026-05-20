package com.seathappens.security.config;

import com.seathappens.security.service.JwtAuthorityConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthorityConverter jwtAuthorityConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwtAuthorityConverter);

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        .requestMatchers(
                                "/api/venues/**",
                                "/api/events/**",
                                "/api/ticket-types/**",
                                "/api/inventories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                "/api/reservations/**",
                                "/api/orders/**",
                                "/api/payments/**",
                                "/api/tickets/**"
                        ).hasAnyRole("CUSTOMER", "ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(authenticationConverter)
                        )
                )
                .build();
    }

}
