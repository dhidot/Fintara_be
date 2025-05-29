package com.fintara.repositories;

import com.fintara.models.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.UUID;

public interface DebtRepository extends JpaRepository<LoanRequest, UUID> {

    // Query untuk menghitung jumlah pinjaman aktif berdasarkan customerId
    @Query("""
    SELECT COUNT(l)
    FROM LoanRequest l
    WHERE l.customer.id = :customerId
      AND l.status.name = 'DISBURSED'
      AND NOT EXISTS (
          SELECT rs
          FROM RepaymentSchedule rs
          WHERE rs.loanRequest = l
            AND rs.paidAt IS NULL
      )
""")
    Integer countFullyPaidLoansByCustomerId(UUID customerId);


    // Query untuk mendapatkan total repayment untuk user (mengambil data yang belum dibayar)
    @Query("SELECT SUM(r.amountToPay - r.amountPaid) FROM RepaymentSchedule r WHERE r.loanRequest.customer.id = :customerId AND r.paidAt IS NULL")
    BigDecimal getTotalRepaymentByCustomerId(UUID customerId);

    // Query untuk mendapatkan remaining plafond dari CustomerDetails
    @Query("SELECT c.remainingPlafond FROM CustomerDetails c WHERE c.user.id = :customerId")
    BigDecimal getRemainingPlafondByCustomerId(UUID customerId);
}