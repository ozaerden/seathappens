package com.seathappens.security.service;

import com.seathappens.security.config.JwtProperties;
import com.seathappens.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public GeneratedToken generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expiresInSeconds());
        String jti = UUID.randomUUID().toString();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        String accessToken = jwtEncoder.encode(
                JwtEncoderParameters.from(jwsHeader, claims)
        ).getTokenValue();

        return new GeneratedToken(
                accessToken,
                jti,
                expiresInSeconds()
        );
    }

    public long expiresInSeconds() {
        return jwtProperties.expirationMinutes() * 60;
    }

}