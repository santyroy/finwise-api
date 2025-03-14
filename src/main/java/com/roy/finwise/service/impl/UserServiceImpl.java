package com.roy.finwise.service.impl;

import com.roy.finwise.dto.UserRequest;
import com.roy.finwise.dto.UserResponse;
import com.roy.finwise.entity.User;
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
        return null;
    }

    @Override
    public UserResponse findUserByEmail(String email) {
        return null;
    }

    @Override
    public UserResponse updateUserByEmail(String email, UserRequest userRequest) {
        return null;
    }

    @Override
    public void deleteUserByEmail(String email) {

    }
}
