package com.sakuBCA.dtos.customerDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerProfileUpdateDTO {
    private String ttl;
    private String ktpUrl;
    private String alamat;
    private String noTelp;
    private String nik;
    private String namaIbuKandung;
    private String pekerjaan;
    private Double gaji;
    private String noRek;
    private String statusRumah;
}
