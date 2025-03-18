package com.roy.finwise.service.impl;

import com.roy.finwise.dto.*;
import com.roy.finwise.entity.Role;
import com.roy.finwise.entity.User;
import com.roy.finwise.exceptions.CustomAuthenticationException;
import com.roy.finwise.exceptions.NotFoundException;
import com.roy.finwise.exceptions.UserAlreadyExistException;
import com.roy.finwise.repository.RoleRepository;
import com.roy.finwise.repository.UserRepository;
import com.roy.finwise.security.service.JwtService;
import com.roy.finwise.service.AuthService;
import com.roy.finwise.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Transactional
    @Override
    public UserResponse registerUser(UserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            log.error("User with email: {} already exist", request.getEmail());
            throw new UserAlreadyExistException("User with email: " + request.getEmail() + " already exist");
        }
        User newUser = MapperUtil.userDtoToEntity(request);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        Role role = roleRepository.findByName("USER").orElseGet(() -> roleRepository.save(new Role("USER")));
        newUser.setRoles(Set.of(role));
        User savedUser = userRepository.save(newUser);
        log.info("Successfully created user with ID: {}", savedUser.getId());
        return MapperUtil.userEntityToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("Logging user with email: {}", request.email());
        try {
            // Authenticate the user
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            // Set authentication to security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            UserDetails principal = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateToken(principal);
            String refreshToken = jwtService.generateRefreshToken(principal);

            // Get user
            UserResponse userResponse = getUser(principal.getUsername());

            return new LoginResponse(accessToken, refreshToken, userResponse.getName(), userResponse.getEmail(), userResponse.getRoles());

        } catch (BadCredentialsException | InternalAuthenticationServiceException ex) {
            log.error("Authentication failed for email: {}", request.email(), ex);
            throw new CustomAuthenticationException("Invalid Credentials.");
        }
    }

    private UserResponse getUser(String email) {
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
