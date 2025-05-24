package com.fintara.dtos.loanRequestDTO;

import com.fintara.models.LoanRequest;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class LoanInProgressResponseDTO {
    private UUID customerId;
    private String customerName;
    private UUID loanRequestId;
    private BigDecimal amount;
    private Integer tenor;
    private BigDecimal interestAmount;
    private BigDecimal interestRate;
    private BigDecimal feesAmount;
    private String branchName;
    private String marketingName;
    private String marketingEmail;
    private String status;

    public static LoanInProgressResponseDTO fromEntity(LoanRequest loanRequest) {
        LoanInProgressResponseDTO dto = new LoanInProgressResponseDTO();
        dto.setCustomerId(loanRequest.getCustomer().getUser().getId());
        dto.setCustomerName(loanRequest.getCustomer().getUser().getName());
        dto.setLoanRequestId(loanRequest.getId());
        dto.setAmount(loanRequest.getAmount());
        dto.setTenor(loanRequest.getTenor());
        dto.setInterestAmount(loanRequest.getInterestAmount());
        dto.setInterestRate(loanRequest.getInterestRate());
        dto.setFeesAmount(loanRequest.getFeesAmount());
        dto.setBranchName(loanRequest.getBranch() != null ? loanRequest.getBranch().getName() : null);
        dto.setMarketingName(loanRequest.getMarketing() != null ? loanRequest.getMarketing().getName() : null);
        dto.setMarketingEmail(loanRequest.getMarketing() != null ? loanRequest.getMarketing().getEmail() : null);
        dto.setStatus(loanRequest.getStatus() != null ? loanRequest.getStatus().getName() : null);
        return dto;
    }

}


