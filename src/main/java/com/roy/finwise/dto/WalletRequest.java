package com.roy.finwise.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record WalletRequest(
        @NotBlank(message = "Wallet name is mandatory")
        String name,
        @DecimalMin(value = "0.0", message = "Spending limit amount should not be negative")
        BigDecimal spendingLimits,
        @NotBlank(message = "User Id is mandatory")
        String userId) {
}
