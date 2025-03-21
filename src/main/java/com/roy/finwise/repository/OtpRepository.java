package com.roy.finwise.repository;

import com.roy.finwise.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {
    Optional<Otp> findByEmail(String email);
    void deleteByEmail(String email);
}
