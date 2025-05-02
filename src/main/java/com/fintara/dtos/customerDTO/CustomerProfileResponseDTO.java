package com.fintara.dtos.customerDTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CustomerProfileResponseDTO {
    private String name;
    private String email;

    private LocalDate ttl;
    private String alamat;
    private String noTelp;
    private String nik;
    private String namaIbuKandung;
    private String pekerjaan;
    private BigDecimal gaji;
    private String noRek;
    private String statusRumah;

}