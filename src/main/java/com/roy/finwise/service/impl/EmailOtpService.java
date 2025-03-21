package com.roy.finwise.service.impl;

import com.roy.finwise.entity.Otp;
import com.roy.finwise.event.OtpSentEvent;
import com.roy.finwise.repository.OtpRepository;
import com.roy.finwise.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailOtpService implements OtpService {

    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Async
    public void sendOtp(String email) {

        try {
            // Generate a random 6-digit OTP
            String otp = generateOtp();
            log.info("Generated OTP for email: {}", email);

            // Save OTP to database with expiration time
            saveOtp(email, otp);
            log.info("Saved OTP to database for email: {}", email);

            // Send email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Your OTP Code");
            message.setText("Your OTP code is: " + otp + ". It will expire in 10 minutes.");

            mailSender.send(message);
            log.info("Successfully sent OTP email to: {}", email);

            // Publish success event
            eventPublisher.publishEvent(new OtpSentEvent(email, true));
        } catch (Exception e) {
            // Publish failure event
            log.error("Failed to send OTP to {}: {}", email, e.getMessage(), e);
            eventPublisher.publishEvent(new OtpSentEvent(email, false, e.getMessage()));
        }
    }

    @Override
    public boolean validateOtp(String email, String otp) {
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

    private void saveOtp(String email, String otp) {
        Otp otpEntity = Otp.builder()
                .otpNumber(otp)
                .expiry(Instant.now().plus(10, ChronoUnit.MINUTES))  // 10 minute expiry
                .email(email)
                .build();

        // If there's an existing OTP, delete it first
        otpRepository.deleteByEmail(email);
        otpRepository.save(otpEntity);
    }

}
