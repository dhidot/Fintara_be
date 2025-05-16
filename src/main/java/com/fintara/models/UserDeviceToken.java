package com.fintara.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_device_tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id"})
})
@Data
public class UserDeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relasi ke User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "fcm_token", nullable = false)
    private String fcmToken;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;
}
