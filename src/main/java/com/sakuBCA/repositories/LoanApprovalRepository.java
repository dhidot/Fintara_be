package com.sakuBCA.repositories;

import com.sakuBCA.models.LoanApproval;
import com.sakuBCA.models.LoanRequest;
import com.sakuBCA.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanApprovalRepository extends JpaRepository<LoanApproval, UUID> {
    List<LoanApproval> findByLoanRequest(LoanRequest loanRequest);

    List<LoanApproval> findByLoanRequestId(UUID loanRequestId);

    @Query("SELECT la FROM LoanApproval la WHERE la.handledBy.id = :userId")
    List<LoanApproval> findAllByHandledBy(UUID userId);
}

