package com.fintara.dtos.authDTO;

import lombok.Data;

@Data
public class GoogleLoginRequestDTO {
    private String fcmToken;
    private String deviceInfo;
}
