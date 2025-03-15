package com.roy.finwise.service.impl;

import com.roy.finwise.dto.UserRequest;
import com.roy.finwise.dto.UserResponse;
import com.roy.finwise.entity.User;
import com.roy.finwise.exceptions.NotFoundException;
import com.roy.finwise.exceptions.UserAlreadyExistException;
import com.roy.finwise.repository.UserRepository;
import com.roy.finwise.service.UserService;
import com.roy.finwise.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        log.info("Creating user with email: {}", userRequest.getEmail());
        userRequest.setPassword(hashPassword(userRequest.getPassword()));
        User newUser = MapperUtil.userDtoToEntity(userRequest);
        Optional<User> userOpt = userRepository.findByEmail(newUser.getEmail());
        if (userOpt.isPresent()) {
            log.error("User with email: {} already exist", userRequest.getEmail());
            throw new UserAlreadyExistException("User with email: " + newUser.getEmail() + " already exists");
        }
        User savedUser = userRepository.save(newUser);
        log.info("User saved with ID: {}", savedUser.getId());
        return MapperUtil.userEntityToDto(savedUser);
    }

    @Override
    public UserResponse findUserById(String userId) {
        return MapperUtil.userEntityToDto(findById(userId));
    }

    @Override
    public UserResponse findUserByEmail(String email) {
        return MapperUtil.userEntityToDto(findByEmail(email));
    }

    @Override
    public UserResponse updateUserByEmail(String email, UserRequest userRequest) {
        User existingUser = findByEmail(email);
        if (userRequest.getName() != null) {
            existingUser.setName(userRequest.getName());
        }
        if (userRequest.getEmail() != null) {
            existingUser.setEmail(userRequest.getEmail());
        }
        if (userRequest.getPassword() != null) {
            existingUser.setPassword(hashPassword(userRequest.getPassword()));
        }
        if (userRequest.getMobileNumber() != null) {
            existingUser.setMobileNumber(userRequest.getMobileNumber());
        }
        User updatedUser = userRepository.save(existingUser);
        return MapperUtil.userEntityToDto(updatedUser);
    }

    @Override
    public void deleteUserByEmail(String email) {
        userRepository.delete(findByEmail(email));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User with email: {} does not exist", email);
            return new NotFoundException("User with email: " + email + " not found");
        });
    }

    protected User findById(String userId) {
        return userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> {
            log.error("User with ID: {} does not exist", userId);
            return new NotFoundException("User with ID: " + userId + " not found");
        });
    }

    private String hashPassword(String password) {
        // TODO: Add actual implementation
        return password;
    }
}
