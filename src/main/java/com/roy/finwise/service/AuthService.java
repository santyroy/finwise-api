package com.roy.finwise.service;

import com.roy.finwise.dto.*;
import jakarta.validation.Valid;

public interface AuthService {
    UserResponse registerUser(UserRequest request);
    LoginResponse login(LoginRequest request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    boolean confirmUserSignup(SignupConfirmRequest request);
    void resendOtp(@Valid ResendOTP request);
}
