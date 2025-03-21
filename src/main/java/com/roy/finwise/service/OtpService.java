package com.roy.finwise.service;

public interface OtpService {
    void sendOtp(String email);
    boolean validateOtp(String email, String otp);
}
