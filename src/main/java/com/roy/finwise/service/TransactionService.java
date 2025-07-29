package com.roy.finwise.service;

import com.roy.finwise.dto.TransactionRequest;
import com.roy.finwise.dto.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public interface TransactionService {

    TransactionResponse createTransaction(TransactionRequest transactionRequest);
    Page<TransactionResponse> getAllTransactions(String userId, int pageNo, int pageSize, String direction, String properties);
    TransactionResponse updateTransaction(String transactionId, TransactionRequest transactionRequest);
    void deleteTransaction(String transactionId);
    Slice<TransactionResponse> getAllTransactionsBetweenDates(String userId, int pageNo, int pageSize, String direction, String startDate, String endDate);
}
