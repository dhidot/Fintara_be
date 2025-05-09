package com.fintara.dtos.customerDTO;

import com.fintara.enums.JenisKelamin;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FirstTimeUpdateDTO {
    private JenisKelamin jenisKelamin;
    private String ttl;
    private String alamat;
    private String noTelp;
    private String nik;
    private String namaIbuKandung;
    private String pekerjaan;
    private Double gaji;
    private String noRek;
    private String statusRumah;
}