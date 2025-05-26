package com.fintara.services;

import com.fintara.exceptions.CustomException;
import com.fintara.dtos.loanApprovalDTO.LoanApprovalHistoryResponse;
import com.fintara.models.LoanApproval;
import com.fintara.models.LoanRequest;
import com.fintara.models.LoanStatus;
import com.fintara.models.User;
import com.fintara.repositories.LoanApprovalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanApprovalService {
    @Autowired
    private LoanApprovalRepository loanApprovalRepository;
    @Autowired
    private LoanRequestService loanRequestService;
    @Autowired
    private LoanStatusService loanStatusService;
    @Autowired
    private UserService userService;

    /**
     * Approve a loan request with the given decision.
     *
     * @param loanRequestId The ID of the loan request to approve.
     * @param userId        The ID of the user making the decision (marketing/BM).
     * @param decision      The decision made (REJECT, RECOMMEND, APPROVE, DISBURSED).
     * @return The created LoanApproval entity.
     */
    @Transactional
    public LoanApproval approveLoan(UUID loanRequestId, UUID userId, String decision) {
        // 1️⃣ Cek apakah loan request ada
        LoanRequest loanRequest = loanRequestService.findById(loanRequestId);

        // 2️⃣ Cek apakah user (marketing/BM) ada
        User approver = userService.findById(userId);

        // 3️⃣ Tentukan status baru berdasarkan keputusan
        LoanStatus newStatus;
        if (decision.equalsIgnoreCase("REJECT")) {
            newStatus = loanStatusService.findByName("DITOLAK");
        } else if (decision.equalsIgnoreCase("RECOMMEND")) {
            newStatus = loanStatusService.findByName("RECOMMENDED");
        } else if (decision.equalsIgnoreCase("APPROVE")) {
            newStatus = loanStatusService.findByName("DITERIMA");
        } else if (decision.equalsIgnoreCase("DISBURSED")) {
            newStatus = loanStatusService.findByName("DISBURSED");
        } else {
            throw new CustomException("Keputusan tidak valid!", HttpStatus.BAD_REQUEST);
        }

        // 4️⃣ Simpan approval baru
        LoanApproval approval = LoanApproval.builder()
                .loanRequest(loanRequest)
                .handledBy(approver)
                .status(newStatus)
                .approvedAt(LocalDateTime.now())
                .build();

        loanApprovalRepository.save(approval);

        // 5️⃣ Update status di LoanRequest
        loanRequest.setStatus(newStatus);
        loanRequestService.saveLoanRequest(loanRequest);

        return approval;
    }

    // Mendapatkan daftar persetujuan pinjaman berdasarkan
    public List<LoanApproval> getLoanApprovals(UUID loanRequestId) {
        LoanRequest loanRequest = loanRequestService.findById(loanRequestId);
        return loanApprovalRepository.findByLoanRequest(loanRequest);
    }

    // Save a new LoanApproval
    public LoanApproval save(LoanApproval loanApproval) {
        return loanApprovalRepository.save(loanApproval);
    }

    // Find by loanRequestId
    public List<LoanApproval> findByLoanRequestId(UUID loanRequestId) {
        return loanApprovalRepository.findByLoanRequestId(loanRequestId);
    }

    // Mendapatkan riwayat persetujuan pinjaman yang ditangani oleh pengguna tertentu
    public List<LoanApprovalHistoryResponse> getHandledApprovalsByUser(UUID userId) {
        List<LoanApproval> approvals = loanApprovalRepository.findAllByHandledBy(userId);
        return approvals.stream()
                .map(LoanApprovalHistoryResponse::fromEntity)
                .toList();
    }

    // Count Distinct Loan Requests by Handled By Certain User
    public int countDistinctLoanRequestsByHandledBy(UUID userId) {
        return loanApprovalRepository.countDistinctLoanRequestByHandledBy(userId);
    }
}
