package com.sakuBCA.dtos.authDTO;

import com.sakuBCA.config.validators.ValidPassword;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Password lama wajib diisi")
    private String oldPassword;

    @ValidPassword
    @NotBlank(message = "Password baru wajib diisi")
    private String newPassword;

    @NotBlank(message = "Konfirmasi password baru wajib diisi")
    private String confirmNewPassword;
}