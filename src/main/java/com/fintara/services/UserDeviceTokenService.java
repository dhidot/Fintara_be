package com.fintara.services;

import com.fintara.models.User;
import com.fintara.models.UserDeviceToken;
import com.fintara.repositories.UserDeviceTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserDeviceTokenService {

    @Autowired
    private UserDeviceTokenRepository userDeviceTokenRepository;

    @Transactional
    public void saveOrReplaceToken(User user, String fcmToken, String deviceInfo) {
        // Hapus token lama
        userDeviceTokenRepository.deleteByUser_Id(user.getId());

        // Paksa flush supaya delete langsung dieksekusi ke DB
        userDeviceTokenRepository.flush();

        // Simpan token baru
        UserDeviceToken token = new UserDeviceToken();
        token.setUser(user);
        token.setFcmToken(fcmToken);
        token.setDeviceInfo(deviceInfo);
        token.setLastLogin(LocalDateTime.now());

        userDeviceTokenRepository.save(token);
    }


    //find by id
    public UserDeviceToken findById(UUID id) {
        return userDeviceTokenRepository.findByUser_Id(id).orElse(null);
    }
}
