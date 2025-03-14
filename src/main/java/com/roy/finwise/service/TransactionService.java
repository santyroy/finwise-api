package com.roy.finwise.service;

import com.roy.finwise.dto.TransactionRequest;
import com.roy.finwise.dto.TransactionResponse;

public interface TransactionService {

    TransactionResponse createTransaction(TransactionRequest transactionRequest);
}
