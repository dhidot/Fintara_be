package com.fintara.dtos.repaymentsDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepaymentsScheduleDTO {
    private String id;
    private Integer installmentNumber;
    private BigDecimal amountToPay;
    private BigDecimal amountPaid;
    private LocalDate dueDate;
    private Boolean isLate;
    private BigDecimal penaltyAmount;
    private LocalDate paidAt;
}

