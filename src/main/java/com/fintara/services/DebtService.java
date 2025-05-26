package com.fintara.services;

import com.fintara.dtos.customerDTO.DebtInfoResponseDTO;
import com.fintara.models.CustomerDetails;
import com.fintara.models.RepaymentSchedule;
import com.fintara.models.User;
import com.fintara.repositories.CustomerDetailsRepository;
import com.fintara.repositories.DebtRepository;
import com.fintara.repositories.RepaymentScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class DebtService {
    @Autowired
    private DebtRepository debtRepository;

    public DebtInfoResponseDTO getDebtInfo(User currentUser) {
        UUID userId = currentUser.getId();  // Ambil ID dari User yang sedang login
        UUID customerId = currentUser.getCustomerDetails().getId();  // Ambil ID dari User yang sedang login
        BigDecimal remainingPlafond = debtRepository.getRemainingPlafondByCustomerId(userId);
        Integer activeLoansCount = debtRepository.countActiveLoansByCustomerId(customerId);
        BigDecimal totalRepayment = debtRepository.getTotalRepaymentByCustomerId(customerId);

        return new DebtInfoResponseDTO(remainingPlafond, activeLoansCount, totalRepayment);
    }
}
