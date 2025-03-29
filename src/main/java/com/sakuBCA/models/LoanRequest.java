package com.sakuBCA.models;

import com.sakuBCA.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    private User customer;

    private BigDecimal amount;
    private Integer tenor;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;
}
