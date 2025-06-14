package com.roy.finwise.util;

import com.roy.finwise.entity.OtpPurpose;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OtpUtil {

    @Value("${application.otp.purpose}")
    private String otpPurposeEnv;

    private Set<OtpPurpose> allowedPurpose;

    @PostConstruct
    private void initAllowedPurpose() {
        this.allowedPurpose = Arrays.stream(otpPurposeEnv.split(","))
                .map(String::trim)
                .map(otpPurpose ->
                        OtpPurpose.from(otpPurpose)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP purpose: " + otpPurpose)))
                .collect(Collectors.toSet());
    }

    public OtpPurpose resolveAllowedPurpose(String purpose) {
        return OtpPurpose.from(purpose)
                .filter(otpPurpose -> this.allowedPurpose.contains(otpPurpose))
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP purpose: " + purpose));
    }
}
