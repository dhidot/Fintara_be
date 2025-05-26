package com.fintara.repositories;

import com.fintara.models.LoanApproval;
import com.fintara.models.LoanRequest;
import io.lettuce.core.dynamic.annotation.Param;
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

    // Hitung berapa LoanRequest unik yang sudah dicek oleh user tertentu
    @Query("SELECT COUNT(DISTINCT la.loanRequest.id) FROM LoanApproval la WHERE la.handledBy.id = :userId")
    int countDistinctLoanRequestByHandledBy(@Param("userId") UUID userId);

}

