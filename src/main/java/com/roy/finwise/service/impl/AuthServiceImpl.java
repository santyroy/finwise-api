package com.roy.finwise.service.impl;

import com.roy.finwise.dto.LoginRequest;
import com.roy.finwise.dto.LoginResponse;
import com.roy.finwise.dto.UserRequest;
import com.roy.finwise.dto.UserResponse;
import com.roy.finwise.entity.RefreshToken;
import com.roy.finwise.entity.Role;
import com.roy.finwise.entity.User;
import com.roy.finwise.exceptions.CustomAuthenticationException;
import com.roy.finwise.exceptions.NotFoundException;
import com.roy.finwise.exceptions.UserAlreadyExistException;
import com.roy.finwise.repository.RoleRepository;
import com.roy.finwise.repository.UserRepository;
import com.roy.finwise.security.service.JwtService;
import com.roy.finwise.service.AuthService;
import com.roy.finwise.service.RefreshTokenRepository;
import com.roy.finwise.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

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
    public LoginResponse login(LoginRequest request) {
        log.info("Logging user with email: {}", request.email());
        try {
            // Authenticate the user
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            // Set authentication to security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails principal = (UserDetails) authentication.getPrincipal();

            // Get user
            User user = getUser(principal.getUsername());
            Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toUnmodifiableSet());

            // Generate JWT tokens
            String accessToken = jwtService.generateAccessToken(request.email(), null);
            String refreshToken = generateRefreshToken(user);

            return new LoginResponse(accessToken, refreshToken, user.getName(), user.getEmail(), roles);

        } catch (BadCredentialsException | InternalAuthenticationServiceException ex) {
            log.error("Authentication failed for email: {}", request.email(), ex);
            throw new CustomAuthenticationException("Invalid Credentials.");
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " not found"));
    }

    private String generateRefreshToken(User user) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[64];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .expiration(Instant.now().plusMillis(refreshTokenExpiration))
                .user(user)
                .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }
}
