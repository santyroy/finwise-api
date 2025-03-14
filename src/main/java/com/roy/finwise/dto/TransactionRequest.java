package com.roy.finwise.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionRequest {

    @NotNull(message = "Transaction type is mandatory")
    @Pattern(regexp = "(CREDIT|DEBIT)", message = "Transaction should be either DEBIT or CREDIT")
    private String type;

    @DecimalMin(value = "0.0", inclusive = false, message = "Transaction amount should be created than 0")
    private BigDecimal amount;

    private String category;
    private String description;
    private Set<String> tags;

    @NotNull(message = "UserId is mandatory")
    private String userId;
}
