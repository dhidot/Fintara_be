package com.fintara.dtos.loanRequestDTO;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LoanRequestApprovalDTO {
    //    Data pinjaman customer
    private UUID id;
    private BigDecimal amount;
    private Integer tenor;
    private String customerJob;
    private BigDecimal customerSalary;
    private String status;
    private LocalDateTime requestDate;

    //    Data customer
    private String customerKtpPhotoUrl;
    private String customerSelfieKtpPhotoUrl;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;

    private String marketingNotes;
    private String bmNotes;
    private String backOfficeNotes;
}
