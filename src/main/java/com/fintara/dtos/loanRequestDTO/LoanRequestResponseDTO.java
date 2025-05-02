package com.fintara.dtos.loanRequestDTO;


import com.fintara.models.LoanRequest;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class LoanRequestResponseDTO {

    private UUID customerId;
    private String customerName;
    private UUID loanRequestId;
    private BigDecimal amount;
    private Integer tenor;
    private UUID branchId;
    private String branchName;
    private UUID marketingId;
    private String marketingName;
    private String marketingEmail;
    private String marketingNip;
    private String status;

    // Static method to map from LoanRequest entity
    public static LoanRequestResponseDTO fromEntity(LoanRequest loanRequest) {
        return LoanRequestResponseDTO.builder()
                .customerId(loanRequest.getCustomer().getId())
                .customerName(loanRequest.getCustomer().getUser().getName())
                .loanRequestId(loanRequest.getId())
                .amount(loanRequest.getAmount())
                .tenor(loanRequest.getTenor())
                .branchId(loanRequest.getBranch().getId())
                .branchName(loanRequest.getBranch().getName())
                .marketingId(loanRequest.getMarketing().getId())
                .marketingName(loanRequest.getMarketing().getName())
                .marketingEmail(loanRequest.getMarketing().getEmail())
                .marketingNip(loanRequest.getMarketing().getPegawaiDetails().getNip())
                .status(loanRequest.getStatus().getName())
                .build();
    }
}
