package com.fintara.controllers;

import com.fintara.services.FirebaseService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/notifications")
public class NotificationController {

    @Autowired
    private FirebaseService firebaseService;

    // DTO untuk menerima input dari frontend/postman
    public static class NotificationRequest {
        public String fcmToken;
        public String title;
        public String body;
    }

    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationRequest request) {
        try {
            firebaseService.sendNotification(request.fcmToken, request.title, request.body);
            return "Notification sent successfully";
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "Failed to send notification: " + e.getMessage();
        }
    }
}
