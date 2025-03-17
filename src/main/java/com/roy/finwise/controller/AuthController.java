package com.roy.finwise.controller;

import com.roy.finwise.dto.LoginRequest;
import com.roy.finwise.dto.LoginResponse;
import com.roy.finwise.dto.UserRequest;
import com.roy.finwise.dto.UserResponse;
import com.roy.finwise.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserRequest request) {
        UserResponse userResponse = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}
