package com.roy.finwise.controller;

import com.roy.finwise.dto.TransactionRequest;
import com.roy.finwise.dto.TransactionResponse;
import com.roy.finwise.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> addTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse transactionResponse = transactionService.addTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
    }
}
