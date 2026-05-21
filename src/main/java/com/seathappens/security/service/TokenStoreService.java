package com.seathappens.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenStoreService {

    private static final String ACTIVE_TOKEN_PREFIX = "active-token:";
    private static final String USER_TOKENS_PREFIX = "user-tokens:";

    private final StringRedisTemplate redisTemplate;

    public void storeToken(UUID userId, String jti, Duration ttl) {
        String activeTokenKey = activeTokenKey(jti);
        String userTokensKey = userTokensKey(userId);

        redisTemplate.opsForValue().set(activeTokenKey, userId.toString(), ttl);
        redisTemplate.opsForSet().add(userTokensKey, jti);
        redisTemplate.expire(userTokensKey, ttl);
    }

    public boolean isTokenActive(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(activeTokenKey(jti)));
    }

    public void revokeToken(String jti) {
        redisTemplate.delete(activeTokenKey(jti));
    }

    public void revokeTokensByUserId(UUID userId) {
        String userTokensKey = userTokensKey(userId);

        Set<String> jtis = redisTemplate.opsForSet().members(userTokensKey);

        if (jtis == null || jtis.isEmpty()) {
            return;
        }

        for (String jti : jtis) {
            redisTemplate.delete(activeTokenKey(jti));
        }

        redisTemplate.delete(userTokensKey);
    }

    private String activeTokenKey(String jti) {
        return ACTIVE_TOKEN_PREFIX + jti;
    }

    private String userTokensKey(UUID userId) {
        return USER_TOKENS_PREFIX + userId;
    }

}
