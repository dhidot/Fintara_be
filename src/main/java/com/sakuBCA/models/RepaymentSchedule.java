package com.sakuBCA.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "repayment_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_request_id", nullable = false)
    private LoanRequest loanRequest;

    private Integer installmentNumber; // cicilan ke-berapa
    private BigDecimal amountToPay;
    private BigDecimal amountPaid;

    private LocalDate dueDate;

    private Boolean isLate;
    private BigDecimal penaltyAmount;

    private LocalDate paidAt; // null jika belum dibayar
}
