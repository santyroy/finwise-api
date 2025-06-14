package com.roy.finwise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResendOTP(
        @NotBlank(message = "Email is mandatory")
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Incorrect email format")
        String email,
        @NotBlank(message = "OTP Purpose is mandatory")
        String otpPurpose) {
}
