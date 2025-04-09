package com.sakuBCA.dtos.authDTO;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String nip;
    private String oldPassword;
    private String newPassword;
}