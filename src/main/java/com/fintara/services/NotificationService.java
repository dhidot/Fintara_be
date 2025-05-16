package com.fintara.services;

import com.fintara.models.UserDeviceToken;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class NotificationService {

    @Autowired
    private FirebaseService firebaseService;
    @Autowired
    private UserDeviceTokenService userDeviceTokenService;

    // Misal method ini di service yang meng-handle notifikasi
    public void sendNotificationToUser(UUID userId, String title, String body) {
        // Panggil service UserDeviceToken yang return UserDeviceToken (bisa null)
        UserDeviceToken token = userDeviceTokenService.findById(userId);

        if (token != null) {
            try {
                String fcmToken = token.getFcmToken();
                firebaseService.sendNotification(fcmToken, title, body);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        } else {
            // User belum punya token device (belum login di device atau belum registrasi FCM)
            System.out.println("User dengan ID " + userId + " belum punya FCM token.");
            // Bisa handle lain, misal simpan log atau abaikan
        }
    }
}
