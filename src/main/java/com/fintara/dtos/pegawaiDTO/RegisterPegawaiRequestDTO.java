package com.fintara.dtos.pegawaiDTO;

import com.fintara.enums.JenisKelamin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import com.fintara.enums.StatusPegawai;

@Getter
@Setter
public class RegisterPegawaiRequestDTO {
    @NotBlank(message = "Nama harus diisi")
    private String name;

    @NotBlank(message = "Email harus diisi")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Format email tidak valid"
    )
    private String email;

    @NotBlank(message = "Role harus diisi")
    private String role;

    @NotBlank(message = "NIP harus diisi")
    @Pattern(regexp = "^\\d{8}$", message = "NIP harus terdiri dari 8 digit angka")
    private String nip;

    @NotNull(message = "Jenis Kelamin harus diisi")
    private JenisKelamin jenisKelamin; // Menggunakan Enum

    @NotNull(message = "Branch harus diisi")
    private String branchName;
    @NotNull(message = "Status Pegawai harus diisi")
    private StatusPegawai statusPegawai; // Menggunakan Enum
}
