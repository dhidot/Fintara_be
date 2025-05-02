package com.fintara.services;

import com.fintara.models.LoanStatus;
import com.fintara.repositories.LoanStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LoanStatusService {
    @Autowired
    private LoanStatusRepository loanStatusRepository;

    public LoanStatus findByName(String name) {
        return loanStatusRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Loan status not found"));
    }

    public LoanStatus createLoanStatus(LoanStatus loanStatus) {
        return loanStatusRepository.save(loanStatus);
    }

    public List<LoanStatus> getAllLoanStatuses() {
        return loanStatusRepository.findAll();
    }

    public LoanStatus getLoanStatusById(UUID id) {
        return loanStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan Status not found"));
    }

    public LoanStatus updateLoanStatus(UUID id, LoanStatus loanStatus) {
        LoanStatus existingLoanStatus = getLoanStatusById(id);
        existingLoanStatus.setName(loanStatus.getName());
        return loanStatusRepository.save(existingLoanStatus);
    }

    public void deleteLoanStatus(UUID id) {
        loanStatusRepository.deleteById(id);
    }
}
