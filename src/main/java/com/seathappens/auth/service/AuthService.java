package com.seathappens.auth.service;

import com.seathappens.auth.dto.request.LoginRequest;
import com.seathappens.auth.dto.request.RegisterRequest;
import com.seathappens.auth.dto.response.AuthResponse;
import com.seathappens.auth.dto.response.LoginResponse;
import com.seathappens.common.exception.BusinessException;
import com.seathappens.common.exception.ErrorCode;
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

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final TokenStoreService tokenStoreService;

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

    @Transactional(readOnly = true)
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

        tokenStoreService.storeToken(
                user.getId(),
                generatedToken.jti(),
                Duration.ofSeconds(generatedToken.expiresInSeconds())
        );

        return new LoginResponse(
                generatedToken.accessToken(),
                TOKEN_TYPE,
                generatedToken.expiresInSeconds()
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

        tokenStoreService.revokeToken(jti);
    }

}