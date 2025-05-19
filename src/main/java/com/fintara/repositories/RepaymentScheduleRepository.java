package com.fintara.repositories;

import com.fintara.models.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, UUID> {
    List<RepaymentSchedule> findByLoanRequestId(UUID loanRequestId);

    List<RepaymentSchedule> findByPaidAtIsNullAndDueDateBefore(LocalDate date);

    List<RepaymentSchedule> findByPaidAtIsNull();

    @Query("""
    SELECT COALESCE(SUM(rs.amountPaid), 0) 
    FROM RepaymentSchedule rs
    WHERE rs.loanRequest.customer.id = :customerId
      AND rs.loanRequest.status.name = 'DISBURSED'
    """)
    BigDecimal getTotalAmountPaidByCustomer(@Param("customerId") UUID customerId);


}
