package com.sakuBCA.services;

import com.sakuBCA.models.LoanStatus;
import com.sakuBCA.repositories.LoanStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanStatusService {
    @Autowired
    private LoanStatusRepository loanStatusRepository;

    public LoanStatus findByName(String name) {
        return loanStatusRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Loan status not found"));
    }
}
