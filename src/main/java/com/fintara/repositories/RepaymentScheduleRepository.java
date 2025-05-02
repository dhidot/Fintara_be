package com.fintara.repositories;

import com.fintara.models.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, UUID> {
    List<RepaymentSchedule> findByLoanRequestId(UUID loanRequestId);

    List<RepaymentSchedule> findByPaidAtIsNullAndDueDateBefore(LocalDate date);

    List<RepaymentSchedule> findByPaidAtIsNull();

}
