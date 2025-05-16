package com.fintara.dtos.authDTO;

import lombok.Data;

@Data
public class SetPasswordRequest {
    private String password;
    private String confirmPassword;
}
