package com.roy.finwise.event;

import lombok.Getter;

@Getter
public class OtpSentEvent {
    private final String email;
    private final boolean success;
    private final String errorMessage;

    // Constructor for success
    public OtpSentEvent(String email, boolean success) {
        this(email, success, null);
    }

    // Constructor for failure with error message
    public OtpSentEvent(String email, boolean success, String errorMessage) {
        this.email = email;
        this.success = success;
        this.errorMessage = errorMessage;
    }
}
