package com.roy.finwise.dto;

import java.util.Set;

public record LoginResponse(String jwt, String name, String email, Set<String> roles) {
}
