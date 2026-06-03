package com.seathappens.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenStoreService {

    private static final String ACTIVE_TOKEN_PREFIX = "active-token:";
    private static final String USER_TOKENS_PREFIX = "user-tokens:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh-token:";
    private static final String USER_REFRESH_TOKENS_PREFIX = "user-refresh-tokens:";
    private static final String ACCESS_REFRESH_TOKEN_PREFIX = "access-refresh-token:";

    private final StringRedisTemplate redisTemplate;

    public void storeToken(UUID userId, String jti, Duration ttl) {
        String activeTokenKey = activeTokenKey(jti);
        String userTokensKey = userTokensKey(userId);

        redisTemplate.opsForValue().set(activeTokenKey, userId.toString(), ttl);
        redisTemplate.opsForSet().add(userTokensKey, jti);
        redisTemplate.expire(userTokensKey, ttl);
    }

    public void storeRefreshToken(UUID userId, String refreshToken, Duration ttl) {
        String refreshTokenKey = refreshTokenKey(refreshToken);
        String userRefreshTokensKey = userRefreshTokensKey(userId);

        redisTemplate.opsForValue().set(refreshTokenKey, userId.toString(), ttl);
        redisTemplate.opsForSet().add(userRefreshTokensKey, refreshToken);
        redisTemplate.expire(userRefreshTokensKey, ttl);
    }

    public void linkAccessTokenToRefreshToken(String jti, String refreshToken, Duration ttl) {
        redisTemplate.opsForValue().set(accessRefreshTokenKey(jti), refreshToken, ttl);
    }

    public boolean isTokenActive(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(activeTokenKey(jti)));
    }

    public Optional<UUID> getUserIdByRefreshToken(String refreshToken) {
        String userId = redisTemplate.opsForValue().get(refreshTokenKey(refreshToken));

        if (userId == null || userId.isBlank()) {
            return Optional.empty();
        }

        return Optional.of(UUID.fromString(userId));
    }

    public void revokeAccessTokenAndLinkedRefreshToken(String jti) {
        String userId = redisTemplate.opsForValue().get(activeTokenKey(jti));

        redisTemplate.delete(activeTokenKey(jti));

        String refreshToken = redisTemplate.opsForValue().get(accessRefreshTokenKey(jti));

        if (refreshToken != null) {
            revokeRefreshToken(refreshToken);
        }

        redisTemplate.delete(accessRefreshTokenKey(jti));

        if (userId != null && !userId.isBlank()) {
            redisTemplate.opsForSet().remove(userTokensKey(UUID.fromString(userId)), jti);
        }
    }

    public void revokeRefreshToken(String refreshToken) {
        Optional<UUID> userId = getUserIdByRefreshToken(refreshToken);

        redisTemplate.delete(refreshTokenKey(refreshToken));

        userId.ifPresent(id ->
                redisTemplate.opsForSet().remove(userRefreshTokensKey(id), refreshToken)
        );
    }

    public void revokeTokensByUserId(UUID userId) {
        String userTokensKey = userTokensKey(userId);
        String userRefreshTokensKey = userRefreshTokensKey(userId);

        Set<String> jtis = redisTemplate.opsForSet().members(userTokensKey);

        if (jtis != null && !jtis.isEmpty()) {
            for (String jti : jtis) {
                redisTemplate.delete(activeTokenKey(jti));
                redisTemplate.delete(accessRefreshTokenKey(jti));
            }
        }

        Set<String> refreshTokens = redisTemplate.opsForSet().members(userRefreshTokensKey);

        if (refreshTokens != null && !refreshTokens.isEmpty()) {
            for (String refreshToken : refreshTokens) {
                redisTemplate.delete(refreshTokenKey(refreshToken));
            }
        }

        redisTemplate.delete(userTokensKey);
        redisTemplate.delete(userRefreshTokensKey);
    }

    private String activeTokenKey(String jti) {
        return ACTIVE_TOKEN_PREFIX + jti;
    }

    private String userTokensKey(UUID userId) {
        return USER_TOKENS_PREFIX + userId;
    }

    private String refreshTokenKey(String refreshToken) {
        return REFRESH_TOKEN_PREFIX + refreshToken;
    }

    private String userRefreshTokensKey(UUID userId) {
        return USER_REFRESH_TOKENS_PREFIX + userId;
    }

    private String accessRefreshTokenKey(String jti) {
        return ACCESS_REFRESH_TOKEN_PREFIX + jti;
    }

}
