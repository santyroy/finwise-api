package com.roy.finwise.service.impl;

import com.roy.finwise.dto.UserRequest;
import com.roy.finwise.dto.UserResponse;
import com.roy.finwise.entity.User;
import com.roy.finwise.exceptions.NotFoundException;
import com.roy.finwise.exceptions.UserAlreadyExistException;
import com.roy.finwise.repository.UserRepository;
import com.roy.finwise.service.UserService;
import com.roy.finwise.util.DtoToEntityUtil;
import com.roy.finwise.util.EntityToDtoUtil;
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
        User newUser = DtoToEntityUtil.userDtoToEntity(userRequest);
        Optional<User> userOpt = userRepository.findByEmail(newUser.getEmail());
        if (userOpt.isPresent()) {
            log.error("User with email: {} already exist", userRequest.getEmail());
            throw new UserAlreadyExistException("User with email: " + newUser.getEmail() + " already exists");
        }
        User savedUser = userRepository.save(newUser);
        log.info("User saved with ID: {}", savedUser.getId());
        return EntityToDtoUtil.userEntityToDto(savedUser);
    }

    @Override
    public UserResponse findUserById(String userId) {
        Optional<User> userOpt = userRepository.findById(UUID.fromString(userId));
        if (userOpt.isEmpty()) {
            log.error("User with ID: {} does not exist", userId);
            throw new NotFoundException("User with ID: " + userId + " not found");
        }
        return EntityToDtoUtil.userEntityToDto(userOpt.get());
    }

    @Override
    public UserResponse findUserByEmail(String email) {
        return EntityToDtoUtil.userEntityToDto(findByEmail(email));
    }

    @Override
    public UserResponse updateUserByEmail(String email, UserRequest userRequest) {
        return null;
    }

    @Override
    public void deleteUserByEmail(String email) {
        userRepository.delete(findByEmail(email));
    }

    private User findByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.orElseThrow(() -> {
            log.error("User with email: {} does not exist", email);
            return new NotFoundException("User with email: " + email + " not found");
        });
    }
}
