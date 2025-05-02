package com.sakuBCA.models;

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
    @JoinColumn(name = "handled_by", nullable = false)
    private User handledBy; // User yang menyetujui (Marketing/BM/Back Office)

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private LoanStatus status;

    private String notes;
    private String notesIdentitas;
    private String notesPlafond;
    private String notesSummary;
    private LocalDateTime approvedAt;
}
