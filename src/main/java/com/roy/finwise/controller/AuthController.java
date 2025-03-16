package com.roy.finwise.controller;

import com.roy.finwise.dto.LoginRequest;
import com.roy.finwise.dto.LoginResponse;
import com.roy.finwise.dto.UserResponse;
import com.roy.finwise.repository.UserRepository;
import com.roy.finwise.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Authenticate the user
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            // Set authentication to security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwt = "implementation-in-progress";

            // Get user
            UserResponse userResponse = authService.getUserDetails(request.email());

            return ResponseEntity.ok(
                    new LoginResponse(jwt, userResponse.getName(), userResponse.getEmail(), userResponse.getRoles()));

        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }
}
