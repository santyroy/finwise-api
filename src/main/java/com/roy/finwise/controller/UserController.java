package com.roy.finwise.controller;

import com.roy.finwise.dto.ApiResponse;
import com.roy.finwise.dto.DashboardResponse;
import com.roy.finwise.dto.UserRequest;
import com.roy.finwise.dto.UserResponse;
import com.roy.finwise.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "All endpoint for user related operation")
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse user = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "User creation successful", user));
    }

    @GetMapping(value = "/id/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String userId) {
        UserResponse user = userService.findUserById(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieval successful", user));
    }

    @GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.findUserByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieval successful", user));
    }

    @PutMapping(value = "{email}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> updateUserByEmail(@PathVariable String email, @Valid @RequestBody UserRequest userRequest) {
        UserResponse user = userService.updateUserByEmail(email, userRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "User update successful", user));
    }

    @DeleteMapping(value = "{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> deleteUserByEmail(@PathVariable String email) {
        userService.deleteUserByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deletion successful", null));
    }

    @GetMapping(value = "{userId}/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardData(@PathVariable String userId,
                                                                           @RequestParam(value = "period", defaultValue = "#{T(java.time.YearMonth).now().toString()}") String period) {
        DashboardResponse response = userService.getDashboardDetailsByUser(userId, period);
        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard data retrieval successful", response));
    }

}
