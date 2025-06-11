package com.roy.finwise.controller;

import com.roy.finwise.dto.*;
import com.roy.finwise.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth/Public API", description = "All endpoint for user sign-up, sign-in related operation")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Endpoint for user to login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    @Operation(summary = "Endpoint for user to signup")
    public ResponseEntity<UserResponse> signup(@RequestBody @Valid SignupRequest request) {
        UserResponse userResponse = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/confirm")
    @Operation(summary = "Endpoint for user to confirm email")
    public ResponseEntity<String> signupConfirmation(@Valid @RequestBody SignupConfirmRequest request) {
        boolean isValid = authService.confirmUserSignup(request);
        return isValid ? ResponseEntity.ok("User registration successful")
                : ResponseEntity.badRequest().body("Invalid OTP or Email");
    }

    @PostMapping("/resendOtp")
    @Operation(summary = "Endpoint for user to resend OTP")
    public ResponseEntity<String> resendOtp(@Valid @RequestBody ResendOTP request) {
        authService.resendOtp(request);
        return ResponseEntity.ok("Otp sent successfully");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Endpoint for user to generate new access token using refresh token")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse refreshTokenResponse = authService.refreshToken(request);
        return ResponseEntity.ok(refreshTokenResponse);
    }
}
