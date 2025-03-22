package com.sakuBCA.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "customer_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class CustomerDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
