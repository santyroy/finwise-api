package com.roy.finwise.service.impl;

import com.roy.finwise.entity.OtpPurpose;
import com.roy.finwise.event.OtpSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${application.otp.expiry}")
    private String otpExpiry;

    @Async
    public void sendEmail(String otp, String email, OtpPurpose otpPurpose) {
        try {
            // Send email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(getEmailSubject(otpPurpose));
            message.setText("Your OTP code is: " + otp + ". It will expire in " + otpExpiry + " minutes.");

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

    private String getEmailSubject(OtpPurpose otpPurpose) {
        return switch (otpPurpose) {
            case ACCOUNT_VERIFICATION -> "Your OTP for Account Verification";
            case PASSWORD_RESET -> "Your OTP for Password Reset";
            case EMAIL_CHANGE -> "Your OTP for Email Change";
        };
    }
}
