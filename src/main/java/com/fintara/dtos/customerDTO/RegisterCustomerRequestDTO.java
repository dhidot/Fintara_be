package com.fintara.dtos.customerDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fintara.validators.UniqueEmail;
import com.fintara.validators.ValidPassword;
import com.fintara.enums.JenisKelamin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterCustomerRequestDTO {

    @JsonProperty("name")
    @NotBlank(message = "Nama tidak boleh kosong")
    private String name;

    @JsonProperty("email")
    @NotBlank(message = "Email tidak boleh kosong")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Format email tidak valid"
    )
    @UniqueEmail
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "Password tidak boleh kosong")
    @ValidPassword
    private String password;
}
