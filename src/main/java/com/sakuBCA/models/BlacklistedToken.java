package com.sakuBCA.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {

    @Id
    private String token;
    private LocalDateTime expiryDate;

    // Constructor Default (wajib untuk JPA)
    public BlacklistedToken() {
    }

    // Constructor yang Sesuai dengan Parameter (String, LocalDateTime)
    public BlacklistedToken(String token, LocalDateTime expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate.plusDays(1);
    }
}
