package com.roy.finwise.dto;

import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {
    private String userId;
    private String name;
    private String email;
    private String mobileNumber;
    private Set<String> roles;
}
