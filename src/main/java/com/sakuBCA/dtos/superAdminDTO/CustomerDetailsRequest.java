package com.sakuBCA.dtos.superAdminDTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CustomerDetailsRequest {
    private LocalDate ttl;
    private String alamat;
    private String noTelp;
    private String nik;
    private String namaIbuKandung;
    private String pekerjaan;
    private BigDecimal gaji;
    private String noRek;
    private String statusRumah;
    private BigDecimal plafond;
}
