package com.roy.finwise.controller;

import com.roy.finwise.dto.PageDTO;
import com.roy.finwise.dto.TransactionRequest;
import com.roy.finwise.dto.TransactionResponse;
import com.roy.finwise.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final PagedResourcesAssembler<TransactionResponse> pagedResourcesAssembler;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse transactionResponse = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
    }
    /*
    NOT RECOMMENDED APPROACH
    Serializing PageImpl instances as-is is not supported,
    meaning that there is no guarantee about the stability of the resulting JSON structure!
    For a stable JSON structure, please use Spring Data's PagedModel
    (globally via @EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO))
    or Spring HATEOAS and Spring Data's PagedResourcesAssembler as
    documented in https://docs.spring.io/spring-data/commons/reference/repositories/core-extensions.html#core.web.pageables.
     */
    @GetMapping("{userId}")
    public ResponseEntity<Page<TransactionResponse>> getAllTransactionsByPage(@PathVariable String userId,
                                                                              @RequestParam(defaultValue = "0") int pageNo,
                                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                                              @RequestParam(defaultValue = "DESC") String direction,
                                                                              @RequestParam(defaultValue = "createdAt") String properties) {
        Page<TransactionResponse> allTransactions = transactionService
                .getAllTransactions(userId, pageNo, pageSize, direction, properties);
        return ResponseEntity.ok(allTransactions);
    }

    /*
    Option 1: Use Custom PageDTO wrapper (Recommended for simplicity)
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<PageDTO<TransactionResponse>> getAllTransactionsByPageDTO(@PathVariable String userId,
                                                                                    @RequestParam(defaultValue = "0") int pageNo,
                                                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                                                    @RequestParam(defaultValue = "DESC") String direction,
                                                                                    @RequestParam(defaultValue = "createdAt") String properties) {
        Page<TransactionResponse> allTransactions = transactionService
                .getAllTransactions(userId, pageNo, pageSize, direction, properties);
        return ResponseEntity.ok(new PageDTO<>(allTransactions));
    }

    /*
    Option 2: Option 2: Use Spring HATEOAS (More powerful but requires additional dependency)
     */
    @GetMapping("/users/{userId}/hateoas")
    public PagedModel<EntityModel<TransactionResponse>> getAllTransactionsByPageHATEOAS(@PathVariable String userId,
                                                                                        @RequestParam(defaultValue = "0") int pageNo,
                                                                                        @RequestParam(defaultValue = "10") int pageSize,
                                                                                        @RequestParam(defaultValue = "DESC") String direction,
                                                                                        @RequestParam(defaultValue = "createdAt") String properties) {
        Page<TransactionResponse> allTransactions = transactionService
                .getAllTransactions(userId, pageNo, pageSize, direction, properties);
        return pagedResourcesAssembler.toModel(allTransactions);
    }

    @PutMapping("{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransactionById(@PathVariable String transactionId,
                                                                     @Valid @RequestBody TransactionRequest request) {
        TransactionResponse transactionResponse = transactionService.updateTransaction(transactionId, request);
        return ResponseEntity.ok(transactionResponse);
    }

    @DeleteMapping("{transactionId}")
    public ResponseEntity<String> deleteTransactionById(@PathVariable String transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.ok("Transaction deleted successfully");
    }
}
