package com.roy.finwise.service;

import com.roy.finwise.dto.LoginRequest;
import com.roy.finwise.dto.LoginResponse;
import com.roy.finwise.dto.UserRequest;
import com.roy.finwise.dto.UserResponse;

public interface AuthService {
    UserResponse registerUser(UserRequest request);
    LoginResponse login(LoginRequest request);
}
