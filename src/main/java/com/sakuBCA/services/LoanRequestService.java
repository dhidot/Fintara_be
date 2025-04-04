package com.sakuBCA.services;

import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.dtos.loanRequestDTO.LoanRequestApprovalDTO;
import com.sakuBCA.models.*;
import com.sakuBCA.repositories.LoanRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanRequestService {
    @Autowired
    private LoanRequestRepository loanRequestRepository;
    @Autowired
    private LoanStatusService loanStatusService;
    @Autowired
    private CustomerDetailsService customerDetailsService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private UserService userService;
    @Autowired
    @Lazy
    private LoanApprovalService loanApprovalService;

    // Get loan request byID
    public LoanRequest findById(UUID id) {
        return loanRequestRepository.findById(id)
                .orElseThrow(() -> new CustomException("Loan request tidak ditemukan!", HttpStatus.NOT_FOUND));
    }

    // Save loan request
    public LoanRequest saveLoanRequest(LoanRequest loanRequest) {
        try {
            return loanRequestRepository.save(loanRequest);
        } catch (Exception e) {
            throw new CustomException("Gagal menyimpan loan request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional
    public LoanRequest createLoanRequest(BigDecimal amount, Integer tenor, double latitude, double longitude) {
        // Ambil user yang sedang terautentikasi
        User currentUser = userService.getAuthenticatedUser();

        // Ambil customer details terkait dengan user
        CustomerDetails customerDetails = currentUser.getCustomerDetails();

        if (customerDetails == null) {
            throw new CustomException("Customer details tidak ditemukan", HttpStatus.NOT_FOUND);
        }

        // Validasi plafond customer
        validatePlafond(customerDetails, amount, tenor);

        // Cari branch terdekat
        UUID branchId = branchService.findNearestBranch(latitude, longitude);
        if (branchId == null) {
            throw new CustomException("Tidak ada cabang terdekat", HttpStatus.BAD_REQUEST);
        }

        // Pilih marketing berdasarkan rotasi
        User assignedMarketing = assignMarketing(branchId);

        // Set status awal "REVIEW"
        LoanStatus pendingStatus = loanStatusService.findByName("REVIEW");

        // Simpan loan request baru
        LoanRequest newRequest = LoanRequest.builder()
                .customer(customerDetails)
                .amount(amount)
                .tenor(tenor)
                .branch(branchService.findById(branchId))
                .marketing(assignedMarketing)
                .requestDate(LocalDateTime.now())
                .status(pendingStatus)
                .build();

        return loanRequestRepository.save(newRequest);
    }

    // Validasi plafond customer
    private void validatePlafond(CustomerDetails customerDetails, BigDecimal amount, Integer tenor) {
        Plafond customerPlafond = customerDetails.getPlafond();

        if (amount.compareTo(customerPlafond.getMaxAmount()) > 0 || tenor > customerPlafond.getMaxTenor()) {
            throw new CustomException("Jumlah pinjaman atau tenor melebihi batas plafond!", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateCustomer(User user) {
        if (!user.getRole().getName().equals("CUSTOMER")) {
            throw new CustomException("Hanya customer yang bisa membuat loan request", HttpStatus.FORBIDDEN);
        }
    }

    private void validateLoanAmountAndTenor(CustomerDetails customerDetails, BigDecimal amount, Integer tenor) {
        Plafond plafond = customerDetails.getPlafond();
        if (amount.compareTo(plafond.getMaxAmount()) > 0 || tenor > plafond.getMaxTenor()) {
            throw new CustomException("Jumlah pinjaman atau tenor melebihi batas plafond!", HttpStatus.BAD_REQUEST);
        }
    }

    private User assignMarketing(UUID branchId) {
        List<User> marketingByBranch = userService.getMarketingByBranch(branchId);
        if (marketingByBranch.isEmpty()) {
            throw new CustomException("Tidak ada marketing yang tersedia di cabang ini", HttpStatus.BAD_REQUEST);
        }

        // Ambil jumlah loan request yang sedang ditangani setiap marketing
        Map<UUID, Long> marketingLoad = loanRequestRepository.countLoanRequestsByMarketing(branchId);

        // Pilih marketing dengan jumlah tugas paling sedikit
        return marketingByBranch.stream()
                .min(Comparator.comparing(marketing -> marketingLoad.getOrDefault(marketing.getId(), 0L)))
                .orElse(marketingByBranch.get(0)); // Jika semua marketing punya jumlah tugas yang sama, pilih yang pertama
    }


    /********** MARKETING APPROVAL **********/
    private LoanRequestApprovalDTO convertToDTO(LoanRequest loanRequest) {
        return LoanRequestApprovalDTO.builder()
                .id(loanRequest.getId())
                .customerName(loanRequest.getCustomer().getUser().getName())
                .amount(loanRequest.getAmount())
                .tenor(loanRequest.getTenor())
                .status(loanRequest.getStatus().getName())
                .requestDate(loanRequest.getRequestDate())
                .build();
    }

    public List<LoanRequestApprovalDTO> getLoanRequestsByMarketing(UUID marketingId) {
        List<LoanRequest> loanRequests = loanRequestRepository.findByMarketingId(marketingId);

        return loanRequests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void reviewLoanRequest(UUID loanRequestId, UUID marketingId, Boolean isApproved, String notes) {
        // 1️⃣ Ambil loan request yang bersangkutan
        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new CustomException("Loan request tidak ditemukan", HttpStatus.NOT_FOUND));

        // 2️⃣ Cek apakah marketing yang login sesuai dengan yang menangani request ini
        if (!loanRequest.getMarketing().getId().equals(marketingId)) {
            throw new CustomException("Anda tidak berhak mereview loan request ini", HttpStatus.FORBIDDEN);
        }

        // 3️⃣ Update status berdasarkan hasil review
        LoanStatus status;
        if (isApproved) {
            status = loanStatusService.findByName("DIREKOMENDASIKAN_MARKETING");
            loanRequest.setStatus(status);
            loanRequest.setApprovalMarketingAt(LocalDateTime.now());
        } else {
            status = loanStatusService.findByName("DITOLAK_MARKETING");
            loanRequest.setStatus(status);
        }

        loanRequest.setStatus(status);
        saveLoanRequest(loanRequest);

        // 4️⃣ Simpan review di LoanApproval
        LoanApproval loanApproval = LoanApproval.builder()
                .loanRequest(loanRequest)
                .approvedBy(loanRequest.getMarketing()) // Marketing yang mereview
                .status(status)
                .notes(notes) // Bisa jadi alasan penolakan atau catatan lain
                .approvedAt(LocalDateTime.now())
                .build();

        loanApprovalService.save(loanApproval);
    }

    /********** BM APPROVAL **********/
    public List<LoanRequestApprovalDTO> getLoanRequestsForBranchManager(UUID branchManagerId) {
        // 1️⃣ Ambil branch ID dari branch manager
        UUID branchId = userService.getBranchIdByUserId(branchManagerId);

        // 2️⃣ Ambil semua loan request di branch ini dengan status "APPROVED_MARKETING"
        List<LoanRequest> loanRequests = loanRequestRepository.findByBranchIdAndStatus(branchId, "DIREKOMENDASIKAN_MARKETING");

        // 3️⃣ Konversi ke DTO agar response lebih rapi
        return loanRequests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void reviewLoanRequestByBM(UUID loanRequestId, UUID branchManagerId, Boolean isApproved, String notes) {
        // 1️⃣ Ambil loan request
        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new CustomException("Loan request tidak ditemukan", HttpStatus.NOT_FOUND));

        // 2️⃣ Cek apakah BM berhak mereview (harus di branch yang sama)
        UUID branchId = userService.getBranchIdByUserId(branchManagerId);
        if (!loanRequest.getBranch().getId().equals(branchId)) {
            throw new CustomException("Anda tidak berhak mereview loan request ini", HttpStatus.FORBIDDEN);
        }

        // 3️⃣ Update status berdasarkan review BM
        LoanStatus newStatus;
        if (isApproved) {
            newStatus = loanStatusService.findByName("DISETUJUI_BM"); // Approved oleh BM
        } else {
            newStatus = loanStatusService.findByName("DITOLAK_BM"); // Ditolak oleh BM
        }

        loanRequest.setStatus(newStatus);
        loanRequest.setApprovalBMAt(LocalDateTime.now()); // Timestamp approval BM

        // 4️⃣ Simpan history approval BM di loan_approvals
        LoanApproval approvalRecord = LoanApproval.builder()
                .loanRequest(loanRequest)
                .approvedBy(userService.findById(branchManagerId)) // BM yang approve
                .status(newStatus)
                .notes(notes)
                .approvedAt(LocalDateTime.now())
                .build();
        loanApprovalService.save(approvalRecord);

        // 5️⃣ Simpan perubahan ke database
        loanRequestRepository.save(loanRequest);
    }

    /********* BACK OFFICE DISBURSE **********/
    public List<LoanRequestApprovalDTO> getLoanRequestsForBackOffice(UUID backOfficeId) {
        // 1️⃣ Ambil branch ID dari Back Office yang login
        UUID branchId = userService.getBranchIdByUserId(backOfficeId);

        // 2️⃣ Ambil semua loan request dengan status "DISETUJUI_BM" di branch ini
        List<LoanRequest> loanRequests = loanRequestRepository.findByBranchIdAndStatus(branchId, "DISETUJUI_BM");

        // 3️⃣ Konversi ke DTO agar response lebih rapi
        return loanRequests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public void disburseLoanRequest(UUID loanRequestId, UUID backOfficeId) {
        // 1️⃣ Ambil loan request
        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new CustomException("Loan request tidak ditemukan", HttpStatus.NOT_FOUND));

        // 2️⃣ Pastikan statusnya sudah DISETUJUI_BM
        if (!loanRequest.getStatus().getName().equals("DISETUJUI_BM")) {
            throw new CustomException("Loan request belum siap untuk dicairkan", HttpStatus.BAD_REQUEST);
        }

        // 3️⃣ Validasi apakah user ini adalah Back Office dan dicabang yang sama dengan branch manager yang approve
        UUID branchId = userService.getBranchIdByUserId(backOfficeId);
        if (!loanRequest.getBranch().getId().equals(branchId)) {
            throw new CustomException("Anda tidak berhak mereview loan request ini", HttpStatus.FORBIDDEN);
        }

        // 4️⃣ Update status menjadi DISBURSED dan set waktu disbursement
        LoanStatus disbursedStatus = loanStatusService.findByName("DISBURSED");
        loanRequest.setStatus(disbursedStatus);
        loanRequest.setDisbursedAt(LocalDateTime.now());

        // 5️⃣ Simpan record approval oleh BO di loan_approvals
        LoanApproval approvalRecord = LoanApproval.builder()
                .loanRequest(loanRequest)
                .approvedBy(userService.findById(backOfficeId)) // Back Office yang approve
                .status(disbursedStatus)
                .notes("Dana telah dicairkan.")
                .approvedAt(LocalDateTime.now())
                .build();
        loanApprovalService.save(approvalRecord);

        // 6️⃣ Simpan perubahan ke database
        loanRequestRepository.save(loanRequest);
    }
}

