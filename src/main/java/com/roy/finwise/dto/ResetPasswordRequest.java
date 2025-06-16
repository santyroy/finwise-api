package com.roy.finwise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
        @NotBlank(message = "Email is mandatory")
        @Schema(description = "Enter user's email")
        String email,
        @NotBlank(message = "Password is mandatory")
        @Schema(description = "Password user's email")
        String password,
        @NotBlank(message = "OTP cannot be empty")
        @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
        String otp,
        @NotBlank(message = "OTP Purpose is mandatory")
        String otpPurpose
) {
}