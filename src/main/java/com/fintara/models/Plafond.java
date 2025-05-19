package com.fintara.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "plafonds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plafond {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    @NotNull(message = "Nama plafon tidak boleh kosong")
    private String name; // Bronze, Silver, Gold, Platinum

    @Column(nullable = false)
    @NotNull(message = "Maksimum Amount tidak boleh kosong")
    private BigDecimal maxAmount; // Maksimum jumlah pinjaman

    @Column(nullable = false)
    @NotNull(message = "Interest Rate tidak boleh kosong")
    private BigDecimal interestRate; // Bunga dalam persen (misal: 0.05 untuk 5%)

    @Column(nullable = false)
    private BigDecimal feeRate; // contoh: 0.03 = 3% admin fee

    @Column(nullable = false)
    @NotNull(message = "Minimal tenor tidak boleh kosong")
    private Integer minTenor; // Minimal tenor dalam bulan

    @Column(nullable = false)
    @NotNull(message = "Maksimal tenor tidak boleh kosong")
    private Integer maxTenor; // Maksimal tenor dalam bulan
}
