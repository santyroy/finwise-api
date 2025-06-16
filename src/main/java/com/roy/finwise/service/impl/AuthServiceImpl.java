package com.roy.finwise.service.impl;

import com.roy.finwise.dto.*;
import com.roy.finwise.entity.OtpPurpose;
import com.roy.finwise.entity.RefreshToken;
import com.roy.finwise.entity.Role;
import com.roy.finwise.entity.User;
import com.roy.finwise.exceptions.*;
import com.roy.finwise.repository.RefreshTokenRepository;
import com.roy.finwise.repository.RoleRepository;
import com.roy.finwise.repository.UserRepository;
import com.roy.finwise.security.service.JwtService;
import com.roy.finwise.service.AuthService;
import com.roy.finwise.service.OtpService;
import com.roy.finwise.util.MapperUtil;
import com.roy.finwise.util.OtpUtil;
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

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
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
    private final OtpService otpService;
    private final OtpUtil otpUtil;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Override
    public UserResponse registerUser(SignupRequest request) {
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
        otpService.sendOtp(request.getEmail(), OtpPurpose.ACCOUNT_VERIFICATION);
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

            // Delete the old refresh token from DB
            Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByUser(user);
            refreshTokenOpt.ifPresent(refreshTokenRepository::delete);
            // Flush to ensure delete is processed immediately
            refreshTokenRepository.flush();

            // Generate Access tokens
            String accessToken = jwtService.generateAccessToken(request.email(), null);
            // Generate Refresh token
            String refreshToken = generateRefreshToken(user);

            return new LoginResponse(accessToken, refreshToken, user.getId().toString(), user.getName(), user.getEmail(), roles);

        } catch (BadCredentialsException | InternalAuthenticationServiceException ex) {
            log.error("Authentication failed for email: {}", request.email(), ex);
            throw new CustomAuthenticationException("Invalid Credentials.");
        }
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken oldRefreshToken = validateRefreshToken(request.refreshToken());
        UUID userId = oldRefreshToken.getUser().getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        // Rotate refresh token (more secure)
        // Delete old refresh token
        refreshTokenRepository.delete(oldRefreshToken);
        // Flush to ensure delete is processed immediately
        refreshTokenRepository.flush();

        // Generate new access token
        String accessToken = jwtService.generateAccessToken(user.getEmail(), null);

        // Generate new refresh token
        String refreshToken = generateRefreshToken(user);

        return new RefreshTokenResponse(accessToken, refreshToken);
    }

    @Override
    public boolean confirmUserSignup(SignupConfirmRequest request) {
        OtpPurpose otpPurpose = otpUtil.resolveAllowedPurpose(request.otpPurpose());
        boolean isValid = otpService.validateOtp(request.email(), request.otp(), otpPurpose);
        if (isValid) {
            User user = getUser(request.email());
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void resendOtp(ResendOTP request) {
        OtpPurpose otpPurpose = otpUtil.resolveAllowedPurpose(request.otpPurpose());
        otpService.sendOtp(request.email(), otpPurpose);
    }

    @Override
    public boolean resetPassword(ResetPasswordRequest request) {
        User user = getUser(request.email());
        OtpPurpose otpPurpose = otpUtil.resolveAllowedPurpose(request.otpPurpose());
        boolean isValid = otpService.validateOtp(user.getEmail(), request.otp(), otpPurpose);
        if (isValid) {
            user.setPassword(passwordEncoder.encode(request.password()));
            userRepository.save(user);
            return true;
        }
        return false;
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

    private RefreshToken validateRefreshToken(String refreshToken) {
        // Find the stored refresh token
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new NotFoundException("Refresh token not found"));

        // Check if token is expired
        if (storedToken.getExpiration().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new TokenExpiredException("Refresh token expired");
        }

        // Validate the token belongs to a valid user
        User user = userRepository.findById(storedToken.getUser().getId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Check if user is still active
        if (!user.isEnabled()) {
            throw new UserInactiveException("User account is inactive");
        }

        return storedToken;
    }
}
