package com.sakuBCA.repositories;
import com.sakuBCA.models.BlacklistedToken;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, String> {
    boolean existsByToken(String token);

    void deleteAllByExpiryDateBefore(LocalDateTime now);

}
