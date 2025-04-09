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

    private LocalDateTime requestDate;
    private LocalDateTime approvalMarketingAt;
    private LocalDateTime approvalBMAt;
    private LocalDateTime disbursedAt;

    // ❌ removed: repaymentStatus, remainingAmount, monthlyInstallment, dueDate, paidOffDate
}
