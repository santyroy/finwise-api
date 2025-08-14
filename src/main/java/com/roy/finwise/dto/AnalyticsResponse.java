package com.roy.finwise.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AnalyticsResponse {
    private TransactionAnalytics transactionAnalytics;
    private CategoryAnalytics categoryAnalytics;
}
