package com.roy.finwise.dto;

import lombok.*;

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
}
