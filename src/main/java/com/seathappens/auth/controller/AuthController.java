package com.seathappens.auth.controller;

import com.seathappens.auth.dto.request.LoginRequest;
import com.seathappens.auth.dto.request.RefreshTokenRequest;
import com.seathappens.auth.dto.request.RegisterRequest;
import com.seathappens.auth.dto.response.AuthResponse;
import com.seathappens.auth.dto.response.LoginResponse;
import com.seathappens.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "Authentication APIs")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register user.")
    @PostMapping(value = "/register", version = "1")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary = "Login user.")
    @PostMapping(value = "/login", version = "1")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Refresh access token.")
    @PostMapping(value = "/refresh", version = "1")
    public LoginResponse refreshAccessToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshAccessToken(request);
    }

    @Operation(summary = "Logout user.")
    @PostMapping(value = "/logout", version = "1")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
        authService.logout();
    }

}
