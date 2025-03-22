package com.sakuBCA.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CustomerDetailsDTO {
    private Integer id;
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

    public CustomerDetailsDTO(Integer id, LocalDate ttl, String alamat, String noTelp, String nik,
                              String namaIbuKandung, String pekerjaan, BigDecimal gaji, String noRek,
                              String statusRumah, BigDecimal plafond) {
        this.id = id;
        this.ttl = ttl;
        this.alamat = alamat;
        this.noTelp = noTelp;
        this.nik = nik;
        this.namaIbuKandung = namaIbuKandung;
        this.pekerjaan = pekerjaan;
        this.gaji = gaji;
        this.noRek = noRek;
        this.statusRumah = statusRumah;
        this.plafond = plafond;
    }
}
