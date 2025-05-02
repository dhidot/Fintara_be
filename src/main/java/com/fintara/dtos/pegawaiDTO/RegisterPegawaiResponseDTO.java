package com.fintara.dtos.pegawaiDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterPegawaiResponseDTO {
    private String email;
    private String message;
}
