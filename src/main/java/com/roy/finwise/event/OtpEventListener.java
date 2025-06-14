package com.roy.finwise.event;

import com.roy.finwise.entity.Otp;
import com.roy.finwise.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpEventListener {

    private final OtpRepository otpRepository;

    @EventListener
    public void handleOtpSentEvent(OtpSentEvent event) {
        if (event.isSuccess()) {
            handleSuccessfulOtpSent(event.getEmail());
        } else {
            handleFailedOtpSent(event.getEmail(), event.getErrorMessage());
        }
    }

    private void handleSuccessfulOtpSent(String email) {
        log.info("OTP successfully sent to: {}", email);

        // Update database to mark email as sent
        Otp otpEntity = otpRepository.findByEmail(email).orElse(null);
        if (otpEntity != null) {
            otpEntity.setEmailSent(true);
            otpEntity.setSentAt(Instant.now());
            otpRepository.save(otpEntity);
        }

        // Additional processing like analytics, notifications, etc.
        updateAnalytics(email, true);
    }

    private void handleFailedOtpSent(String email, String errorMessage) {
        log.error("Failed to send OTP to: {}. Reason: {}", email, errorMessage);

        // Update database to mark as failed
        Otp otpEntity = otpRepository.findByEmail(email).orElse(null);
        if (otpEntity != null) {
            otpEntity.setEmailSent(false);
            otpEntity.setDeliveryFailureReason(errorMessage);
            otpRepository.save(otpEntity);
        }

        // Additional error handling like retries, alerting, etc.
        if (shouldRetry(errorMessage)) {
            scheduleRetry(email);
        }

        updateAnalytics(email, false);
    }

    private boolean shouldRetry(String errorMessage) {
        // Don't retry for invalid recipient errors
        if (errorMessage.contains("does not exist") ||
                errorMessage.contains("mailbox unavailable") ||
                errorMessage.contains("550 5.1.1")) {
            return false;
        }

        // Don't retry for blocked/rejected emails
        return !errorMessage.contains("blocked") &&
                !errorMessage.contains("rejected") &&
                !errorMessage.contains("550 5.7.1");

        // Retry for temporary failures like connection issues
    }

    private void scheduleRetry(String email) {
        // Logic to schedule a retry
        log.info("Scheduling retry for email: {}", email);
        // Implementation depends on your retry mechanism
    }

    private void updateAnalytics(String email, boolean success) {
        // Update metrics, analytics, etc.
        log.info("OTP send status: {} for email: {}", success, email);
    }
}
