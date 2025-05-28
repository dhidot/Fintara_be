package com.fintara.dtos.loanApprovalDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanApprovalReviewerResponse {
    private UUID id;
    private UUID handledById;
    private String handledByName;
    private String handledByRole;
    private String status;
    private String notesIdentitas;
    private String notesPlafond;
    private String notesSummary;
    private LocalDateTime approvedAt;
}
