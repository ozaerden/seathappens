package com.seathappens.security.service;

public record GeneratedToken(
        String accessToken,
        String jti,
        long expiresInSeconds
) {
}
