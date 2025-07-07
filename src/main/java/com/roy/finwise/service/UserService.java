package com.roy.finwise.service;

import com.roy.finwise.dto.DashboardResponse;
import com.roy.finwise.dto.UserRequest;
import com.roy.finwise.dto.UserResponse;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);
    UserResponse findUserById(String userId);
    UserResponse findUserByEmail(String email);
    UserResponse updateUserByEmail(String email, UserRequest userRequest);
    void deleteUserByEmail(String email);
    DashboardResponse getDashboardDetailsByUser(String userId, String period, int pageNo, int pageSize);
}
