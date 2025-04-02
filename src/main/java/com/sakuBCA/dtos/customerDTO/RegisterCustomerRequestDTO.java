package com.sakuBCA.dtos.customerDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 8, message = "Password minimal 8 karakter")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password harus mengandung huruf besar, huruf kecil, dan angka"
    )
    private String password;
}
