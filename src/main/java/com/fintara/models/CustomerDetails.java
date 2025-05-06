package com.fintara.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate ttl;
    @Column(name = "ktp_url")
    private String ktpUrl;
    @Column(name = "selfie_ktp_url")
    private String selfieKtpUrl;
    private String alamat;
    private String noTelp;
    private String nik;
    private String namaIbuKandung;
    private String pekerjaan;
    private BigDecimal gaji;
    private String noRek;
    private String statusRumah;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plafond_id", nullable = false)
    private Plafond plafond;

    @Column(name = "remaining_plafond", nullable = false)
    private BigDecimal remainingPlafond;
}
