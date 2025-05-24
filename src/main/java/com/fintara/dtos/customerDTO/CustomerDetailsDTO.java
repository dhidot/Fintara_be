package com.fintara.dtos.customerDTO;

import com.fintara.enums.JenisKelamin;
import com.fintara.models.CustomerDetails;
import com.fintara.models.Plafond;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CustomerDetailsDTO {
    private UUID id;
    private JenisKelamin jenisKelamin;
    private LocalDate ttl;
    private String alamat;
    private String noTelp;
    private String nik;
    private String namaIbuKandung;
    private String pekerjaan;
    private BigDecimal gaji;
    private String noRek;
    private String statusRumah;
    private String ktpUrl;
    private String selfieKtpUrl;
    private Plafond plafond;
    private LocalDateTime updatedAt;

    public CustomerDetailsDTO(CustomerDetails customerDetails) {
        this.id = customerDetails.getId();
        this.jenisKelamin = customerDetails.getJenisKelamin();
        this.ttl = customerDetails.getTtl();
        this.alamat = customerDetails.getAlamat();
        this.noTelp = customerDetails.getNoTelp();
        this.nik = customerDetails.getNik();
        this.namaIbuKandung = customerDetails.getNamaIbuKandung();
        this.pekerjaan = customerDetails.getPekerjaan();
        this.gaji = customerDetails.getGaji();
        this.noRek = customerDetails.getNoRek();
        this.statusRumah = customerDetails.getStatusRumah();
        this.ktpUrl = customerDetails.getKtpUrl();
        this.selfieKtpUrl = customerDetails.getSelfieKtpUrl();
        this.plafond = customerDetails.getPlafond();
        this.updatedAt = customerDetails.getUpdatedAt();
    }
}
