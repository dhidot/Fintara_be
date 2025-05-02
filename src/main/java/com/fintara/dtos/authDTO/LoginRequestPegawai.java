package com.fintara.dtos.authDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class LoginRequestPegawai {
    @NotBlank(message = "NIP tidak boleh kosong")
    private String nip;

    @JsonProperty("password")
    @NotBlank(message = "Password tidak boleh kosong")
    private String password;
}
