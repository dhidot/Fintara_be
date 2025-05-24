package com.fintara.dtos.loanRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanHistoryResponseDTO {
    private UUID id;                 // ID pengajuan pinjaman
    private BigDecimal amount;      // Jumlah pinjaman
    private Integer tenor;         // Tenor pinjaman
    private BigDecimal interestAmount; // Jumlah bunga
    private BigDecimal disbursedAmount; // Jumlah yang dicairkan
    private String status;          // Nama status: DISBURSED, DITOLAK_BM, dll
    private LocalDateTime createdAt;
}

