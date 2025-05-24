package com.fintara.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @ManyToOne
    @JoinColumn(name = "plafond_id", nullable = false)
    private Plafond plafond;

    private BigDecimal amount;
    private Integer tenor;

    private BigDecimal disbursedAmount;    // Dana yang benar-benar cair ke nasabah setelah dipotong biaya
    private BigDecimal totalRepaymentAmount; // Total yang harus dibayar nasabah termasuk bunga dan biaya lainnya
    private BigDecimal interestAmount;     // Jumlah bunga yang dikenakan
    private BigDecimal interestRate;       // Bunga dalam persen (misal: 0.05 untuk 5%)
    private BigDecimal feesAmount;
    private BigDecimal estimatedInstallment; // Angsuran per bulan yang harus dibayar nasabah

    private LocalDateTime requestDate;
    private LocalDateTime approvalMarketingAt;
    private LocalDateTime approvalBMAt;
    private LocalDateTime disbursedAt;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
