package com.sakuBCA.dtos.loanRequestDTO;


import lombok.Data;

import java.util.UUID;

@Data
public class LoanApprovalDTO {
    private UUID loanRequestId;
    private UUID userId;
    private String decision; // "REJECT", "RECOMMEND", "APPROVE", "DISBURSED"
}
