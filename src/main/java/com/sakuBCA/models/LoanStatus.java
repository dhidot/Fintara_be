package com.sakuBCA.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "loan_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name; // Contoh: SEDANG_DIREVIEW, DITOLAK, APPROVED, DISBURSED
}
