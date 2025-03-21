package com.roy.finwise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupConfirmRequest(
        @NotBlank(message = "OTP cannot be empty")
        @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
        String otp,
        @NotBlank(message = "Email is mandatory")
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Incorrect email format")
        String email) {
}
