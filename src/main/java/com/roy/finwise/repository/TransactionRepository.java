package com.roy.finwise.repository;

import com.roy.finwise.entity.Transaction;
import com.roy.finwise.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @EntityGraph(attributePaths = {"category", "tags"})
    Page<Transaction> findByUser(@Param("user") User user, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "tags"})
    Slice<Transaction> findTransactionSliceByUserAndCreatedAtBetween(User user, Instant startDate, Instant endDate, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "tags"})
    List<Transaction> findByUserAndCreatedAtBetween(User user, Instant startDate, Instant endDate, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "tags"})
    List<Transaction> findByUserAndCreatedAtBetween(User user, Instant startDate, Instant endDate);

    @EntityGraph(attributePaths = {"category", "tags"})
    Optional<Transaction> findTransactionById(UUID id);
}
