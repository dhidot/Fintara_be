package com.fintara.services;

import com.fintara.models.RepaymentSchedule;
import com.fintara.repositories.RepaymentScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepaymentScheduleService {
    @Autowired
    private RepaymentScheduleRepository repaymentScheduleRepository;

    // save
    public void save(RepaymentSchedule repaymentSchedule) {
        repaymentScheduleRepository.save(repaymentSchedule);
    }

    @Scheduled(cron = "0 0 1 * * ?") // setiap hari jam 1 pagi
    @Transactional
    public void runDailyPenaltyUpdate() {
        updatePenaltyForAllUnpaidSchedules();
    }

    public void updatePenaltyForAllUnpaidSchedules() {
        List<RepaymentSchedule> overdueSchedules = repaymentScheduleRepository
                .findByPaidAtIsNullAndDueDateBefore(LocalDate.now());

        for (RepaymentSchedule schedule : overdueSchedules) {
            long daysLate = ChronoUnit.DAYS.between(schedule.getDueDate(), LocalDate.now());

            // 5% dari angsuran per hari keterlambatan
            BigDecimal dailyPenalty = schedule.getAmountToPay()
                    .multiply(BigDecimal.valueOf(0.05));

            BigDecimal penalty = dailyPenalty.multiply(BigDecimal.valueOf(daysLate));

            schedule.setIsLate(true);
            schedule.setPenaltyAmount(penalty);
        }

        repaymentScheduleRepository.saveAll(overdueSchedules);
    }

}
