package com.roy.finwise.dto;

import com.roy.finwise.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategorySummary {
    private String name;
    private TransactionType type;
    private BigDecimal totalAmount;
}
