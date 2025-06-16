package com.roy.finwise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "Email is mandatory")
        @Schema(description = "Enter user's email")
        String email
) {
}