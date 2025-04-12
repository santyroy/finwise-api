package com.roy.finwise.util;

import com.roy.finwise.dto.*;
import com.roy.finwise.entity.*;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static WalletResponse walletEntityToDto(Wallet wallet) {
        Set<TransactionResponse> transactions = wallet.getTransactions().stream()
                .map(MapperUtil::transactionEntityToDto).collect(Collectors.toSet());
        return WalletResponse.builder()
                .id(wallet.getId().toString())
                .name(wallet.getName())
                .spendingLimits(wallet.getSpendingLimits())
                .transactions(transactions)
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}
