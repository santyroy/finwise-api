package com.roy.finwise.service.impl;

import com.roy.finwise.dto.TransactionRequest;
import com.roy.finwise.dto.TransactionResponse;
import com.roy.finwise.entity.*;
import com.roy.finwise.exceptions.NotFoundException;
import com.roy.finwise.repository.CategoryRepository;
import com.roy.finwise.repository.TransactionRepository;
import com.roy.finwise.repository.WalletRepository;
import com.roy.finwise.service.TransactionService;
import com.roy.finwise.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final WalletRepository walletRepository;
    private final UserServiceImpl userService;

    @Override
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {
        log.info("New Transaction for user {}", transactionRequest.getUserId());
        Category category = findCategoryByName(transactionRequest.getCategory().toUpperCase());
        User user = userService.findById(transactionRequest.getUserId());
        Transaction newTransaction = MapperUtil.transactionDtoToEntity(transactionRequest, category, user);
        if(transactionRequest.getWalletId() != null && !transactionRequest.getWalletId().isEmpty()) {
            Optional<Wallet> walletOpt = walletRepository.findById(UUID.fromString(transactionRequest.getWalletId()));
            walletOpt.ifPresent(newTransaction::setWallet);
        }
        Transaction savedTransaction = transactionRepository.save(newTransaction);
        log.info("Transaction saved with ID: {}", savedTransaction.getId());
        return MapperUtil.transactionEntityToDto(savedTransaction);
    }

    @Override
    public Page<TransactionResponse> getAllTransactions(String userId, int pageNo, int pageSize,
                                                        String direction, String properties) {

        pageNo = Math.max(pageNo, 0);
        pageSize = Math.max(pageSize, 1);
        direction = direction.equalsIgnoreCase("ASC") ? "ASC" : "DESC";
        properties = List.of("type", "amount", "createdAt", "tags").contains(properties) ? properties : "createdAt";

        try {
            User user = userService.findById(userId);
            if (user != null) {
                PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.Direction.valueOf(direction), properties);
                Page<Transaction> transactions = transactionRepository.findByUser(user, pageRequest);
                return transactions.map(MapperUtil::transactionEntityToDto);
            }
            return Page.empty();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }
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
        if(transactionRequest.getWalletId() != null && !transactionRequest.getWalletId().isEmpty()) {
            Optional<Wallet> walletOpt = walletRepository.findById(UUID.fromString(transactionRequest.getWalletId()));
            walletOpt.ifPresent(existingTransaction::setWallet);
        }
        if(transactionRequest.getCreatedAt() != null) {
            existingTransaction.setCreatedAt(transactionRequest.getCreatedAt());
            existingTransaction.setUpdatedAt(transactionRequest.getCreatedAt());
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
        return transactionRepository.findTransactionById(UUID.fromString(transactionId))
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
