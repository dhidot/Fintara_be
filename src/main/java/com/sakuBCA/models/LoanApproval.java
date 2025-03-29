package com.sakuBCA.models;

import com.sakuBCA.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_approvals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "loan_request_id", nullable = false)
    private LoanRequest loanRequest;

    @ManyToOne
    @JoinColumn(name = "approved_by", nullable = false)
    private User approvedBy;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    private LocalDateTime approvedAt;
}
