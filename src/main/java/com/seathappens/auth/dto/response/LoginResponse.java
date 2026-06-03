package com.seathappens.auth.dto.response;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Long expiresInSeconds,
        String refreshToken,
        Long refreshExpiresInSeconds
) {
}
