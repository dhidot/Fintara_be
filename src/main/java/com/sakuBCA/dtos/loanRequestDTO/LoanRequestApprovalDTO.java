package com.sakuBCA.dtos.loanRequestDTO;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LoanRequestApprovalDTO {
    private UUID id;
    private String customerName;
    private BigDecimal amount;
    private Integer tenor;
    private String status;
    private LocalDateTime requestDate;
}
