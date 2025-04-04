package com.sakuBCA.dtos.authDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;
}

