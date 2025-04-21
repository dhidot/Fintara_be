package com.sakuBCA.repositories;

import com.sakuBCA.models.CustomerDetails;
import com.sakuBCA.models.LoanRequest;
import com.sakuBCA.models.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, UUID> {
    List<LoanRequest> findByCustomer(CustomerDetails customer);

    @Query("SELECT lr.marketing.id, COUNT(lr) FROM LoanRequest lr WHERE lr.branch.id = :branchId GROUP BY lr.marketing.id")
    List<Object[]> countLoanRequestsByMarketing(@Param("branchId") UUID branchId);

    @Query("SELECT lr FROM LoanRequest lr WHERE lr.marketing.id = :marketingId AND lr.status.name = 'REVIEW'")
    List<LoanRequest> findByMarketingId(@Param("marketingId") UUID marketingId);

    @Query("SELECT l FROM LoanRequest l WHERE l.branch.id = :branchId AND l.status.name = :status")
    List<LoanRequest> findByBranchIdAndStatus(@Param("branchId") UUID branchId, @Param("status") String status);
}
