package com.roy.finwise.dto;

import java.util.Set;

public record LoginResponse(String accessToken, String refreshToken, String name, String email, Set<String> roles) {
}
