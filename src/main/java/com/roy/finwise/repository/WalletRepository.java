package com.roy.finwise.repository;

import com.roy.finwise.entity.User;
import com.roy.finwise.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    List<Wallet> findByUser(User user);
}
