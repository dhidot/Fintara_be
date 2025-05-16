package com.fintara.repositories;

import com.fintara.models.User;
import com.fintara.models.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, UUID> {
    void deleteByUser_Id(UUID userId);

    Optional<UserDeviceToken> findByUser_Id(UUID userId);

}
