package com.roy.finwise.service;

import com.roy.finwise.dto.*;

public interface AuthService {
    UserResponse registerUser(SignupRequest request);
    LoginResponse login(LoginRequest request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    boolean confirmUserSignup(SignupConfirmRequest request);
    void resendOtp(ResendOTP request);
    boolean resetPassword(ResetPasswordRequest request);
}
