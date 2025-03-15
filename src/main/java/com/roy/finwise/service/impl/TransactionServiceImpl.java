package com.roy.finwise.service.impl;

import com.roy.finwise.dto.TransactionRequest;
import com.roy.finwise.dto.TransactionResponse;
import com.roy.finwise.entity.Category;
import com.roy.finwise.entity.Transaction;
import com.roy.finwise.entity.TransactionType;
import com.roy.finwise.entity.User;
import com.roy.finwise.exceptions.NotFoundException;
import com.roy.finwise.repository.CategoryRepository;
import com.roy.finwise.repository.TransactionRepository;
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
    private final UserServiceImpl userService;

    @Override
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {
        log.info("New Transaction for user {}", transactionRequest.getUserId());
        Category category = findCategoryByName(transactionRequest.getCategory().toUpperCase());
        User user = userService.findById(transactionRequest.getUserId());
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
        Transaction existingTransaction = findByTransactionId(transactionId);
        if (transactionRequest.getType() != null) {
            existingTransaction.setType(TransactionType.valueOf(transactionRequest.getType()));
        }
        if (transactionRequest.getAmount() != null) {
            existingTransaction.setAmount(transactionRequest.getAmount());
        }
        if (transactionRequest.getCategory() != null) {
            existingTransaction.setCategory(findCategoryByName(transactionRequest.getCategory()));
        }
        if (transactionRequest.getDescription() != null) {
            existingTransaction.setDescription(transactionRequest.getDescription());
        }
        if (transactionRequest.getTags() != null && !transactionRequest.getTags().isEmpty()) {
            existingTransaction.setTags(transactionRequest.getTags());
        }
        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        return MapperUtil.transactionEntityToDto(updatedTransaction);
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

    private Category findCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName.toUpperCase())
                .orElseThrow(() -> new NotFoundException("Category " + categoryName + " not found"));
    }
}
