package com.sakuBCA.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}

