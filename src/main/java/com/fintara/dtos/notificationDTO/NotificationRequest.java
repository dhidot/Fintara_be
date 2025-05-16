package com.fintara.dtos.notificationDTO;

import lombok.Data;

@Data
public class NotificationRequest {
    private String targetToken;
    private String title;
    private String body;
}
