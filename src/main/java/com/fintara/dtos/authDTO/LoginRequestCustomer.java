package com.fintara.dtos.authDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestCustomer {

    @JsonProperty("email")
    @NotBlank(message = "Email tidak boleh kosong")
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "Password tidak boleh kosong")
    private String password;

    @JsonProperty("fcmToken")
    private String fcmToken;

    @JsonProperty("deviceInfo")
    private String deviceInfo;
}
