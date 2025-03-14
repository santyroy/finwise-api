package com.roy.finwise.util;

import com.roy.finwise.dto.TransactionRequest;
import com.roy.finwise.entity.Category;
import com.roy.finwise.entity.Transaction;
import com.roy.finwise.entity.TransactionType;
import com.roy.finwise.entity.User;

import java.time.Instant;

public class DtoToEntityUtil {

    public static Transaction transactionDtoToEntity(TransactionRequest transactionRequest, Category category, User user) {
        Instant now = Instant.now();
        return Transaction.builder()
                .type(TransactionType.valueOf(transactionRequest.getType()))
                .amount(transactionRequest.getAmount())
                .category(category)
                .createdAt(now)
                .updatedAt(now)
                .description(transactionRequest.getDescription())
                .tags(transactionRequest.getTags())
                .user(user)
                .build();
    }
}
