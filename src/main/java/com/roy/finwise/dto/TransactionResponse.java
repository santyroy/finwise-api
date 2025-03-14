package com.roy.finwise.dto;

import com.roy.finwise.entity.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TransactionResponse {

    private UUID id;
    private TransactionType type;
    private BigDecimal amount;
    private String category;
    private String description;
    private Set<String> tags;
}
