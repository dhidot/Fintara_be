package com.fintara.services;

import com.fintara.dtos.repaymentsDTO.RepaymentsScheduleDTO;
import com.fintara.models.CustomerDetails;
import com.fintara.models.LoanRequest;
import com.fintara.models.Plafond;
import com.fintara.models.RepaymentSchedule;
import com.fintara.repositories.CustomerDetailsRepository;
import com.fintara.repositories.PlafondRepository;
import com.fintara.repositories.RepaymentScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RepaymentScheduleService {
    @Autowired
    private RepaymentScheduleRepository repaymentScheduleRepository;
    @Autowired
    private CustomerDetailsRepository customerRepository;
    @Autowired
    private PlafondRepository plafondRepository;

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

    @Transactional
    public void generateRepaymentSchedulesForLoan(LoanRequest loanRequest) {
        BigDecimal principal = loanRequest.getAmount(); // dana yang diajukan konsumen
        int tenor = loanRequest.getTenor();

        Plafond plafond = loanRequest.getPlafond();
        BigDecimal interestRate = plafond.getInterestRate(); // misalnya 0.02 = 2% per bulan

        // Total bunga = pokok x bunga per bulan x tenor
        BigDecimal totalInterest = principal
                .multiply(interestRate)
                .multiply(BigDecimal.valueOf(tenor))
                .setScale(2, RoundingMode.HALF_UP);

        // Total yang harus dibayar konsumen
        BigDecimal totalRepayment = principal.add(totalInterest);

        // Cicilan per bulan
        BigDecimal installmentAmount = totalRepayment
                .divide(BigDecimal.valueOf(tenor), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= tenor; i++) {
            RepaymentSchedule schedule = RepaymentSchedule.builder()
                    .loanRequest(loanRequest)
                    .installmentNumber(i)
                    .amountToPay(installmentAmount)
                    .amountPaid(BigDecimal.ZERO)
                    .dueDate(LocalDate.now().plusMonths(i))
                    .isLate(false)
                    .penaltyAmount(BigDecimal.ZERO)
                    .build();

            repaymentScheduleRepository.save(schedule);
        }
    }

    // Pembayaran Cicilan
    public List<RepaymentsScheduleDTO> getRepaymentByLoanRequestId(UUID loanRequestId) {
        List<RepaymentSchedule> schedules = repaymentScheduleRepository.findByLoanRequestId(loanRequestId);
        return schedules.stream().map(this::toDTO).toList();
    }

    private RepaymentsScheduleDTO toDTO(RepaymentSchedule schedule) {
        return RepaymentsScheduleDTO.builder()
                .id(schedule.getId().toString())
                .installmentNumber(schedule.getInstallmentNumber())
                .amountToPay(schedule.getAmountToPay())
                .amountPaid(schedule.getAmountPaid())
                .dueDate(schedule.getDueDate())
                .isLate(schedule.getIsLate())
                .penaltyAmount(schedule.getPenaltyAmount())
                .paidAt(schedule.getPaidAt())
                .build();
    }

    // Check apakah ada kenaikan plafond
    public void checkAndUpgradePlafondIfEligible(UUID customerId) {
        // 1. Ambil CustomerDetails lengkap dengan Plafond-nya
        CustomerDetails customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        Plafond currentPlafond = customer.getPlafond();

        // 2. Hitung total pelunasan customer
        BigDecimal totalPaid = repaymentScheduleRepository.getTotalAmountPaidByCustomer(customerId);

        // 3. Cek apakah totalPaid > maxAmount currentPlafond
        if (totalPaid.compareTo(currentPlafond.getMaxAmount()) > 0) {
            // 4. Cari plafon berikutnya secara berurutan (Bronze -> Silver -> Gold -> Platinum)
            Plafond nextPlafond = plafondRepository.findNextPlafondByName(currentPlafond.getName());

            if (nextPlafond != null) {
                // 5. Update plafon customer ke nextPlafond
                customer.setPlafond(nextPlafond);
                customerRepository.save(customer);
                // Bisa log upgrade ini, kirim notifikasi, dll

            }
        }
    }
}
