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
import com.roy.finwise.util.DtoToEntityUtil;
import com.roy.finwise.util.EntityToDtoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        Transaction newTransaction = DtoToEntityUtil.transactionDtoToEntity(transactionRequest, category, user);
        Transaction savedTransaction = transactionRepository.save(newTransaction);
        log.info("Transaction saved with ID: {}", savedTransaction.getId());
        return EntityToDtoUtil.transactionEntityToDto(savedTransaction);
    }
}
