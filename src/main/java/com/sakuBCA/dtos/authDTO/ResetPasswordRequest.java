package com.sakuBCA.dtos.authDTO;

import com.sakuBCA.config.validators.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordRequest {
    private String token;
    @NotBlank(message = "Password baru tidak boleh kosong")
    @ValidPassword
    private String newPassword;

    @NotBlank(message = "Konfirmasi password tidak boleh kosong")
    private String confirmPassword;
}

