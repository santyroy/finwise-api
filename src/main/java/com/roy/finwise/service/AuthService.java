package com.roy.finwise.service;

import com.roy.finwise.dto.UserRequest;
import com.roy.finwise.dto.UserResponse;

public interface AuthService {
    UserResponse getUserDetails(String email);
    UserResponse registerUser(UserRequest request);
}
