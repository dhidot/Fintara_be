package com.sakuBCA.dtos.loanRequestDTO;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class LoanRequestDTO {
    private BigDecimal amount;
    private Integer tenor;
    private double latitude;
    private double longitude;
}
