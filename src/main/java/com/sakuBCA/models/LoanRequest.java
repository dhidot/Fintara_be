package com.sakuBCA.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class LoanRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerDetails customer;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private LoanStatus status;

    @ManyToOne
    @JoinColumn(name = "marketing_id", nullable = false)
    private User marketing;

    private BigDecimal amount;  // Jumlah pinjaman
    private Integer tenor;  // Lama cicilan dalam bulan

    private LocalDateTime requestDate;
    private LocalDateTime approvalMarketingAt;  // Waktu approval oleh Marketing
    private LocalDateTime approvalBMAt;         // Waktu approval oleh Branch Manager
    private LocalDateTime disbursedAt;          // Waktu pencairan oleh Back Office
}
