package com.sakuBCA.dtos.superAdminDTO;

import com.sakuBCA.models.CustomerDetails;
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

    public CustomerDetailsDTO(CustomerDetails customerDetails) {
        this.id = customerDetails.getId();
        this.ttl = customerDetails.getTtl();
        this.alamat = customerDetails.getAlamat();
        this.noTelp = customerDetails.getNoTelp();
        this.nik = customerDetails.getNik();
        this.namaIbuKandung = customerDetails.getNamaIbuKandung();
        this.pekerjaan = customerDetails.getPekerjaan();
        this.gaji = customerDetails.getGaji();
        this.noRek = customerDetails.getNoRek();
        this.statusRumah = customerDetails.getStatusRumah();
        this.plafond = customerDetails.getPlafond();
    }
}
