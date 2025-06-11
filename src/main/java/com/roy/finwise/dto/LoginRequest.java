package com.roy.finwise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email is mandatory")
        @Schema(description = "Enter user's email")
        String email,
        @NotBlank(message = "Password is mandatory")
        @Schema(description = "Enter user's password")
        String password) {
}
