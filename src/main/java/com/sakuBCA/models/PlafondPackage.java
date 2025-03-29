package com.sakuBCA.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "plafond_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlafondPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;  // Nama paket (Bronze, Silver, Gold, Platinum)

    private BigDecimal amount;  // Jumlah plafond (5.000.000, 15.000.000, dll)
}