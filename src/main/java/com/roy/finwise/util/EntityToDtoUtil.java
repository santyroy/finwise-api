package com.roy.finwise.util;

import com.roy.finwise.dto.TransactionResponse;
import com.roy.finwise.dto.UserResponse;
import com.roy.finwise.entity.Transaction;
import com.roy.finwise.entity.User;

public class EntityToDtoUtil {

    public static TransactionResponse transactionEntityToDto(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .category(transaction.getCategory())
                .description(transaction.getDescription())
                .tags(transaction.getTags())
                .build();
    }

    public static UserResponse userEntityToDto(User user) {
        return UserResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .build();
    }
}
