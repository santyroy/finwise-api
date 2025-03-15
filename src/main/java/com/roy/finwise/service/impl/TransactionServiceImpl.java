package com.roy.finwise.service.impl;

import com.roy.finwise.dto.TransactionRequest;
import com.roy.finwise.dto.TransactionResponse;
import com.roy.finwise.entity.Category;
import com.roy.finwise.entity.Transaction;
import com.roy.finwise.entity.User;
import com.roy.finwise.exceptions.NotFoundException;
import com.roy.finwise.repository.CategoryRepository;
import com.roy.finwise.repository.TransactionRepository;
import com.roy.finwise.repository.UserRepository;
import com.roy.finwise.service.TransactionService;
import com.roy.finwise.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {
        log.info("New Transaction for user {}", transactionRequest.getUserId());
        Category category = categoryRepository.findByName(transactionRequest.getCategory().toUpperCase())
                .orElseThrow(() -> new NotFoundException("Category " + transactionRequest.getCategory() + " not found"));

        User user = userRepository.findById(UUID.fromString(transactionRequest.getUserId()))
                .orElseThrow(() -> new NotFoundException("User " + transactionRequest.getUserId() + " not found"));

        Transaction newTransaction = MapperUtil.transactionDtoToEntity(transactionRequest, category, user);
        Transaction savedTransaction = transactionRepository.save(newTransaction);
        log.info("Transaction saved with ID: {}", savedTransaction.getId());
        return MapperUtil.transactionEntityToDto(savedTransaction);
    }

    @Override
    public Page<TransactionResponse> getAllTransactions(String userId, Pageable pageable) {
        return null;
    }

    @Override
    public TransactionResponse updateTransaction(String transactionId, TransactionRequest transactionRequest) {
        return null;
    }

    @Override
    public void deleteTransaction(String transactionId) {
        Transaction existingTransaction = findByTransactionId(transactionId);
        transactionRepository.delete(existingTransaction);
    }

    private Transaction findByTransactionId(String transactionId) {
        return transactionRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(() -> {
                    log.error("Transaction with ID: {} does not exist", transactionId);
                    return new NotFoundException("Transaction with ID: " + transactionId + " not found");
                });
    }
}
