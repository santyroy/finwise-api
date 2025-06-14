package com.roy.finwise.service;

import com.roy.finwise.entity.OtpPurpose;

public interface OtpService {
    void sendOtp(String email, OtpPurpose otpPurpose);
    boolean validateOtp(String email, String otp, OtpPurpose otpPurpose);
}
