package com.roy.finwise.controller;

import com.roy.finwise.dto.ApiResponse;
import com.roy.finwise.dto.PageDTO;
import com.roy.finwise.dto.TransactionRequest;
import com.roy.finwise.dto.TransactionResponse;
import com.roy.finwise.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction API", description = "All endpoint for transaction related operation")
public class TransactionController {

    private final TransactionService transactionService;
    private final PagedResourcesAssembler<TransactionResponse> pagedResourcesAssembler;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse transactionResponse = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Transaction creation successful", transactionResponse));
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

    @GetMapping("/users/{userId}/slice")
    public ResponseEntity<Slice<TransactionResponse>> getAllTransactionsBySlice(@PathVariable String userId,
                                                                                @RequestParam(defaultValue = "0") int pageNo,
                                                                                @RequestParam(defaultValue = "10") int pageSize,
                                                                                @RequestParam(defaultValue = "DESC") String direction,
                                                                                @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusMonths(1).toString()}") String startDate,
                                                                                @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().toString()}") String endDate) {
        Slice<TransactionResponse> allTransactions = transactionService.getAllTransactionsBetweenDates(userId, pageNo, pageSize, direction, startDate, endDate);
        return ResponseEntity.ok(allTransactions);
    }

    @PutMapping(value = "{transactionId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransactionById(@PathVariable String transactionId,
                                                                                  @Valid @RequestBody TransactionRequest request) {
        TransactionResponse transactionResponse = transactionService.updateTransaction(transactionId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction update successful", transactionResponse));
    }

    @DeleteMapping(value = "{transactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> deleteTransactionById(@PathVariable String transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction deletion successful", null));
    }
}
