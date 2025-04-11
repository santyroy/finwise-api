package com.roy.finwise.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record WalletResponse(String name, BigDecimal spendingLimits, Set<TransactionResponse> transactions, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
