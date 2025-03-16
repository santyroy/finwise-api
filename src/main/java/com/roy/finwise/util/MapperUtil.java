package com.roy.finwise.util;

import com.roy.finwise.dto.TransactionRequest;
import com.roy.finwise.dto.TransactionResponse;
import com.roy.finwise.dto.UserRequest;
import com.roy.finwise.dto.UserResponse;
import com.roy.finwise.entity.Category;
import com.roy.finwise.entity.Transaction;
import com.roy.finwise.entity.TransactionType;
import com.roy.finwise.entity.User;

import java.time.Instant;

public class MapperUtil {

    private MapperUtil() {}

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

    public static User userDtoToEntity(UserRequest userRequest) {
        return User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .mobileNumber(userRequest.getMobileNumber())
                .build();
    }

    public static TransactionResponse transactionEntityToDto(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .category(transaction.getCategory().getName())
                .description(transaction.getDescription())
                .tags(transaction.getTags())
                .build();
    }

    public static UserResponse userEntityToDto(User user) {
        return UserResponse.builder()
                .userId(user.getId().toString())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .build();
    }
}
