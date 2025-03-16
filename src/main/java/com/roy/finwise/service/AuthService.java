package com.roy.finwise.service;

import com.roy.finwise.dto.UserResponse;

public interface AuthService {
    UserResponse getUserDetails(String email);
}
