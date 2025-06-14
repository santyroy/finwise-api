package com.roy.finwise.entity;

import java.util.Arrays;
import java.util.Optional;

public enum OtpPurpose {
    ACCOUNT_VERIFICATION,
    PASSWORD_RESET,
    EMAIL_CHANGE;


    public static Optional<OtpPurpose> from(String value) {
        return Arrays.stream(values())
                .filter(otpPurpose -> otpPurpose.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
