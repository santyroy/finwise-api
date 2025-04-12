package com.roy.finwise.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record WalletRequest(
        @NotBlank(message = "Wallet name is mandatory")
        String name,
        BigDecimal spendingLimits,
        @NotBlank(message = "User Id is mandatory")
        String userId) {
}
