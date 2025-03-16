package com.roy.finwise.service.impl;

import com.roy.finwise.dto.UserResponse;
import com.roy.finwise.entity.Role;
import com.roy.finwise.entity.User;
import com.roy.finwise.exceptions.NotFoundException;
import com.roy.finwise.repository.UserRepository;
import com.roy.finwise.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getUserDetails(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " not found"));
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toUnmodifiableSet());
        return UserResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .roles(roles)
                .build();
    }
}
