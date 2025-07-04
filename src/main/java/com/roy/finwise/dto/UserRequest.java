package com.roy.finwise.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserRequest {

    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Incorrect email format")
    private String email;

    @Size(min = 8, message = "Password must of minimum 8 characters")
    private String password;

    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobileNumber;

}
