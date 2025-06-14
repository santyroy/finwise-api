package com.roy.finwise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "otps")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String otpNumber;
    private Instant expiry;
    @Column(unique = true)
    private String email;
    @Builder.Default
    private boolean emailSent = false;
    private Instant sentAt;
    private String deliveryFailureReason;
    @Enumerated(EnumType.STRING)
    private OtpPurpose otpPurpose;
}
