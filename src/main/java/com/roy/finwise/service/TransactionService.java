package com.roy.finwise.service;

import com.roy.finwise.dto.TransactionRequest;
import com.roy.finwise.dto.TransactionResponse;
import org.springframework.data.domain.Page;

public interface TransactionService {

    TransactionResponse createTransaction(TransactionRequest transactionRequest);
    Page<TransactionResponse> getAllTransactions(String userId, int pageNo, int pageSize, String direction, String properties);
    TransactionResponse updateTransaction(String transactionId, TransactionRequest transactionRequest);
    void deleteTransaction(String transactionId);
}
