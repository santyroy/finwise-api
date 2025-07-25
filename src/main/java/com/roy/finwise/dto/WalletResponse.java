package com.roy.finwise.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record WalletResponse(String id, String name, BigDecimal spendingLimits, List<TransactionResponse> transactions, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
