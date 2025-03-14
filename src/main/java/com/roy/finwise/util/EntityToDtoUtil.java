package com.roy.finwise.util;

import com.roy.finwise.dto.TransactionResponse;
import com.roy.finwise.entity.Transaction;

public class EntityToDtoUtil {

    public static TransactionResponse TransactionEntityToDto(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .category(transaction.getCategory())
                .description(transaction.getDescription())
                .tags(transaction.getTags())
                .build();
    }
}
