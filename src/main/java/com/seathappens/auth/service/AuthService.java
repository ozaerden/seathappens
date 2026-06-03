package com.seathappens.auth.service;

import com.seathappens.auth.dto.request.LoginRequest;
import com.seathappens.auth.dto.request.RefreshTokenRequest;
import com.seathappens.auth.dto.request.RegisterRequest;
import com.seathappens.auth.dto.response.AuthResponse;
import com.seathappens.auth.dto.response.LoginResponse;
import com.seathappens.common.exception.BusinessException;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.security.config.JwtProperties;
import com.seathappens.security.service.GeneratedToken;
import com.seathappens.security.service.JwtTokenService;
import com.seathappens.security.service.TokenStoreService;
import com.seathappens.user.entity.User;
import com.seathappens.user.entity.UserStatus;
import com.seathappens.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";
    private static final int REFRESH_TOKEN_BYTE_LENGTH = 64;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final TokenStoreService tokenStoreService;
    private final JwtProperties jwtProperties;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getEmail()
        );
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_NOT_ACTIVE);
        }

        GeneratedToken generatedToken = jwtTokenService.generateToken(user);
        String refreshToken = generateRefreshToken();
        Duration accessTokenTtl = Duration.ofSeconds(generatedToken.expiresInSeconds());
        Duration refreshTokenTtl = refreshTokenTtl();

        tokenStoreService.storeToken(
                user.getId(),
                generatedToken.jti(),
                accessTokenTtl
        );
        tokenStoreService.storeRefreshToken(user.getId(), refreshToken, refreshTokenTtl);
        tokenStoreService.linkAccessTokenToRefreshToken(
                generatedToken.jti(),
                refreshToken,
                accessTokenTtl
        );

        return new LoginResponse(
                generatedToken.accessToken(),
                TOKEN_TYPE,
                generatedToken.expiresInSeconds(),
                refreshToken,
                refreshTokenTtl.toSeconds()
        );
    }

    @Transactional
    public LoginResponse refreshAccessToken(RefreshTokenRequest request) {
        User user = tokenStoreService.getUserIdByRefreshToken(request.refreshToken())
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            tokenStoreService.revokeRefreshToken(request.refreshToken());
            throw new BusinessException(ErrorCode.USER_NOT_ACTIVE);
        }

        GeneratedToken generatedToken = jwtTokenService.generateToken(user);
        String refreshToken = generateRefreshToken();
        Duration accessTokenTtl = Duration.ofSeconds(generatedToken.expiresInSeconds());
        Duration refreshTokenTtl = refreshTokenTtl();

        tokenStoreService.revokeRefreshToken(request.refreshToken());
        tokenStoreService.storeToken(
                user.getId(),
                generatedToken.jti(),
                accessTokenTtl
        );
        tokenStoreService.storeRefreshToken(user.getId(), refreshToken, refreshTokenTtl);
        tokenStoreService.linkAccessTokenToRefreshToken(
                generatedToken.jti(),
                refreshToken,
                accessTokenTtl
        );

        return new LoginResponse(
                generatedToken.accessToken(),
                TOKEN_TYPE,
                generatedToken.expiresInSeconds(),
                refreshToken,
                refreshTokenTtl.toSeconds()
        );
    }

    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        String jti = jwt.getId();

        if (jti == null || jti.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        tokenStoreService.revokeAccessTokenAndLinkedRefreshToken(jti);
    }

    private String generateRefreshToken() {
        byte[] bytes = new byte[REFRESH_TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private Duration refreshTokenTtl() {
        return Duration.ofDays(jwtProperties.refreshExpirationDays());
    }

}
