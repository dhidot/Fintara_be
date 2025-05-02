package com.sakuBCA.dtos.loanApprovalDTO;

import com.sakuBCA.models.LoanApproval;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LoanApprovalHistoryResponse {
    private UUID id;
    private String customerName;
    private BigDecimal amount;
    private Integer tenor;
    private String statusName;
    // requestDate
    private LocalDateTime requestDate;
    private LocalDateTime marketingHandledDate;
    private LocalDateTime branchManagerHandledDate;
    private LocalDateTime backOfficeHandledDate;
    // field lain yang kamu butuh

    public static LoanApprovalHistoryResponse fromEntity(LoanApproval entity) {
        return LoanApprovalHistoryResponse.builder()
                .id(entity.getLoanRequest().getId())
                .customerName(entity.getLoanRequest().getCustomer().getUser().getName())
                .amount(entity.getLoanRequest().getAmount())
                .tenor(entity.getLoanRequest().getTenor())
                .statusName(entity.getStatus().getName())
                .requestDate(entity.getLoanRequest().getRequestDate())
                .marketingHandledDate(entity.getLoanRequest().getApprovalMarketingAt())
                .branchManagerHandledDate(entity.getLoanRequest().getApprovalBMAt())
                .backOfficeHandledDate(entity.getLoanRequest().getDisbursedAt())
                .build();
    }
}
