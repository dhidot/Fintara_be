package com.sakuBCA.dtos.authDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class LoginRequest {
    @JsonProperty("email")
    @NotBlank(message = "Email tidak boleh kosong")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Format email tidak valid"
    )
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "Password tidak boleh kosong")
    private String password;
}
