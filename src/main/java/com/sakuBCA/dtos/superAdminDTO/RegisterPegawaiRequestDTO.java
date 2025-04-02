package com.sakuBCA.dtos.superAdminDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import com.sakuBCA.enums.StatusPegawai;

import java.util.UUID;

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
    private String nip;
    @NotNull(message = "Branch harus diisi")
    private UUID branchId;
    @NotNull(message = "Status Pegawai harus diisi")
    private StatusPegawai statusPegawai; // Menggunakan Enum
}
