package com.sakuBCA.dtos.pegawaiDTO;

import com.sakuBCA.models.User;
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
