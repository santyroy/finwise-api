package com.roy.finwise.controller;

import com.roy.finwise.dto.*;
import com.roy.finwise.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Endpoint for user to login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User login successful", loginResponse));
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Endpoint for user to signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@RequestBody @Valid SignupRequest request) {
        UserResponse userResponse = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "User signup successful", userResponse));
    }

    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Endpoint for user to confirm email")
    public ResponseEntity<ApiResponse<String>> signupConfirmation(@Valid @RequestBody SignupConfirmRequest request) {
        boolean isValid = authService.confirmUserSignup(request);
        return isValid ? ResponseEntity.ok(new ApiResponse<>(true, "User verification successful", null))
                : ResponseEntity.badRequest().body(new ApiResponse<>(false, "Invalid OTP or Email", null));
    }

    @PostMapping(value = "/resendOtp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Endpoint for user to resend OTP")
    public ResponseEntity<ApiResponse<String>> resendOtp(@Valid @RequestBody ResendOTP request) {
        authService.resendOtp(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Otp sent successfully", null));
    }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Endpoint for user to generate new access token using refresh token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse refreshTokenResponse = authService.refreshToken(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Refresh token validation successful", refreshTokenResponse));
    }

    @PostMapping(value = "/resetPassword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        boolean isSuccess = authService.resetPassword(request);
        return isSuccess ? ResponseEntity.ok(new ApiResponse<>(true, "Password update successful", null))
                : ResponseEntity.badRequest().body(new ApiResponse<>(false, "Invalid OTP or Email", null));
    }
}
