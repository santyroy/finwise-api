package com.roy.finwise.controller;

import com.roy.finwise.dto.UserRequest;
import com.roy.finwise.dto.UserResponse;
import com.roy.finwise.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse user = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
        UserResponse user = userService.findUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.findUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("{userId}")
    public ResponseEntity<UserResponse> updateUserByEmail(@PathVariable String email, @RequestBody UserRequest userRequest) {
        UserResponse user = userService.updateUserByEmail(email, userRequest);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("{email}")
    public ResponseEntity<String> deleteUserByEmail(@PathVariable String email) {
        userService.deleteUserByEmail(email);
        return ResponseEntity.ok("User deleted successfully");
    }

}
