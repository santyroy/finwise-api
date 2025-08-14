package com.roy.finwise.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionAnalytics {
    private List<TransactionResponse> incomes;
    private List<TransactionResponse> expenses;
}
