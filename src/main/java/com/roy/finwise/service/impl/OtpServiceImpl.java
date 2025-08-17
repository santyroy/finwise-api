package com.roy.finwise.service.impl;

import com.roy.finwise.entity.Otp;
import com.roy.finwise.entity.OtpPurpose;
import com.roy.finwise.repository.OtpRepository;
import com.roy.finwise.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    @Value("${application.otp.expiry}")
    private String otpExpiry;

    @Override
    public void sendOtp(String email, OtpPurpose otpPurpose) {

        // Generate a random 6-digit OTP
        String otp = generateOtp();
        log.info("Generated OTP for email: {}", email);

        // Save OTP to database with expiration time
        saveOtp(email, otp, otpPurpose);
        log.info("Saved OTP to database for email: {}", email);

        // Send email
        emailService.sendMimeEmail(otp, email, otpPurpose);

    }

    @Override
    public boolean validateOtp(String email, String otp, OtpPurpose otpPurpose) {
        Otp otpEntity = otpRepository.findByEmail(email)
                .orElse(null);

        if (otpEntity == null) {
            return false;
        }

        // Check if OTP has expired
        if (Instant.now().isAfter(otpEntity.getExpiry())) {
            otpRepository.delete(otpEntity);
            return false;
        }

        // Check if OTP has doesn't have valid otpPurpose
        if (!otpEntity.getOtpPurpose().equals(otpPurpose)) {
            otpRepository.delete(otpEntity);
            return false;
        }

        // Check if OTP matches
        boolean isValid = otpEntity.getOtpNumber().equals(otp);

        if (isValid) {
            // Delete OTP after validation (one-time use)
            otpRepository.delete(otpEntity);
        }

        return isValid;
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    private void saveOtp(String email, String otp, OtpPurpose otpPurpose) {
        int exp = Integer.parseInt(otpExpiry);
        Otp otpEntity = Otp.builder()
                .otpNumber(otp)
                .expiry(Instant.now().plus(exp, ChronoUnit.MINUTES))
                .email(email)
                .otpPurpose(otpPurpose)
                .build();

        // If there's an existing OTP, delete it first
        otpRepository.deleteByEmail(email);
        otpRepository.flush();
        otpRepository.save(otpEntity);
    }
}
