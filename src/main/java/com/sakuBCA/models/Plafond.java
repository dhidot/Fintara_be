package com.sakuBCA.models;

import jakarta.persistence.*;
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
    private String name; // Bronze, Silver, Gold, Platinum

    @Column(nullable = false)
    private BigDecimal maxAmount; // Maksimum jumlah pinjaman

    @Column(nullable = false)
    private BigDecimal interestRate; // Bunga dalam persen (misal: 0.05 untuk 5%)

    @Column(nullable = false)
    private Integer minTenor; // Minimal tenor dalam bulan

    @Column(nullable = false)
    private Integer maxTenor; // Maksimal tenor dalam bulan
}
