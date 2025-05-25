package com.fintara.dtos.customerDTO;

import com.fintara.enums.JenisKelamin;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FirstTimeUpdateDTO {

    @NotNull(message = "Jenis Kelamin tidak boleh kosong")
    private JenisKelamin jenisKelamin;

    @NotBlank(message = "Tempat Tanggal Lahir tidak boleh kosong")
    private String ttl;

    @NotBlank(message = "Alamat tidak boleh kosong")
    private String alamat;

    @NotBlank(message = "Nomor Telepon tidak boleh kosong")
    private String noTelp;

    @NotBlank(message = "NIK tidak boleh kosong")
    @Size(min = 16, max = 16, message = "NIK harus 16 digit")
    private String nik;

    @NotBlank(message = "Nama Ibu Kandung tidak boleh kosong")
    private String namaIbuKandung;

    @NotBlank(message = "Pekerjaan tidak boleh kosong")
    private String pekerjaan;

    @NotNull(message = "Gaji tidak boleh kosong")
    private Double gaji;

    @NotBlank(message = "Nomor Rekening tidak boleh kosong")
    private String noRek;

    @NotBlank(message = "Status Rumah tidak boleh kosong")
    private String statusRumah;
}
