package com.roy.finwise.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        BigDecimal totalIncomeThisMonth,
        BigDecimal totalExpenseThisMonth,
        List<DashboardWallets> wallets,
        List<TransactionResponse> recentTransactions
) {
}
