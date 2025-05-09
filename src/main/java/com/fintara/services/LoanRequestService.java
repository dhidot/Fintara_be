package com.fintara.services;

import com.fintara.exceptions.CustomException;
import com.fintara.dtos.loanRequestDTO.LoanRequestApprovalDTO;
import com.fintara.dtos.loanRequestDTO.LoanRequestDTO;
import com.fintara.dtos.loanRequestDTO.LoanRequestResponseDTO;
import com.fintara.models.*;
import com.fintara.repositories.LoanRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
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
    @Autowired
    private RepaymentScheduleService repaymentScheduleService;

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
    public LoanRequestResponseDTO createLoanRequest(LoanRequestDTO requestDTO) {
        // Ambil user yang sedang terautentikasi
        User currentUser = userService.getAuthenticatedUser();

        validateCustomer(currentUser);

        // Ambil customer details terkait dengan user
        CustomerDetails customerDetails = currentUser.getCustomerDetails();

        if (customerDetails == null) {
            throw new CustomException("Customer details tidak ditemukan", HttpStatus.NOT_FOUND);
        }

        // Validasi plafond customer
        validatePlafond(customerDetails, requestDTO.getAmount(), requestDTO.getTenor());

        // Cari branch terdekat
        UUID branchId = branchService.findNearestBranch(requestDTO.getLatitude(), requestDTO.getLongitude());
        if (branchId == null) {
            throw new CustomException("Tidak ada cabang terdekat", HttpStatus.BAD_REQUEST);
        }

        // Pilih marketing berdasarkan rotasi
        User assignedMarketing = assignMarketing(branchId);

        // Set status awal "REVIEW"
        LoanStatus pendingStatus = loanStatusService.findByName("REVIEW");

        Plafond customerPlafond = validatePlafond(customerDetails, requestDTO.getAmount(), requestDTO.getTenor());

        LoanRequest newRequest = LoanRequest.builder()
                .customer(customerDetails)
                .amount(requestDTO.getAmount())
                .tenor(requestDTO.getTenor())
                .branch(branchService.findBranchById(branchId))
                .marketing(assignedMarketing)
                .requestDate(LocalDateTime.now())
                .status(pendingStatus)
                .plafond(customerPlafond) // 👈 Ini penting
                .build();

        LoanRequest savedLoanRequest = loanRequestRepository.save(newRequest);

        // Return LoanRequestResponseDTO with the relevant data
        return LoanRequestResponseDTO.fromEntity(savedLoanRequest);
    }

    private Plafond validatePlafond(CustomerDetails customerDetails, BigDecimal amount, int tenor) {
        Plafond plafond = customerDetails.getPlafond();

        // Validasi remainingPlafond
        if (amount.compareTo(customerDetails.getRemainingPlafond()) > 0) {
            throw new CustomException("Sisa plafond tidak mencukupi!", HttpStatus.BAD_REQUEST);
        }

        // Validasi tenor
        if (tenor < plafond.getMinTenor() || tenor > plafond.getMaxTenor()) {
            throw new CustomException("Tenor tidak sesuai dengan batasan plafond!", HttpStatus.BAD_REQUEST);
        }

        return plafond;
    }

    private void validateCustomer(User user) {
        if (!user.getRole().getName().equals("CUSTOMER")) {
            throw new CustomException("Hanya customer yang bisa membuat loan request", HttpStatus.FORBIDDEN);
        }

        CustomerDetails customerDetails = user.getCustomerDetails();
        if (customerDetails == null) {
            throw new CustomException("Data customer belum ditemukan. Harap lengkapi profil terlebih dahulu", HttpStatus.BAD_REQUEST);
        }

        if (isNullOrEmpty(customerDetails.getNik()) ||
                isNullOrEmpty(customerDetails.getAlamat()) ||
                isNullOrEmpty(customerDetails.getNoTelp()) ||
                isNullOrEmpty(customerDetails.getNamaIbuKandung()) ||
                isNullOrEmpty(customerDetails.getPekerjaan()) ||
                customerDetails.getGaji() == null ||
                isNullOrEmpty(customerDetails.getNoRek()) ||
                customerDetails.getStatusRumah() == null) {

            throw new CustomException("Data customer belum lengkap. Harap lengkapi profil sebelum mengajukan pinjaman", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private User assignMarketing(UUID branchId) {
        List<User> marketingByBranch = userService.getMarketingByBranch(branchId);

        if (marketingByBranch.isEmpty()) {
            throw new CustomException("Tidak ada marketing yang tersedia di cabang ini", HttpStatus.BAD_REQUEST);
        }

        // Ambil jumlah loan request yang sedang ditangani setiap marketing
        List<Object[]> rawCounts = loanRequestRepository.countLoanRequestsByMarketing(branchId);
        Map<UUID, Long> marketingLoad = new HashMap<>();

        for (Object[] row : rawCounts) {
            UUID marketingId = (UUID) row[0];
            Long count = (Long) row[1];
            marketingLoad.put(marketingId, count);
        }

        // Pilih marketing dengan jumlah tugas paling sedikit
        return marketingByBranch.stream()
                .min(Comparator.comparing(marketing -> marketingLoad.getOrDefault(marketing.getId(), 0L)))
                .orElse(marketingByBranch.get(0)); // Jika semua marketing punya jumlah tugas yang sama, pilih yang pertama
    }

    public void validateAccess(User currentUser, LoanRequest loanRequest) {
        // Ambil status dari LoanRequest, status merupakan entitas LoanStatus
        LoanStatus loanStatus = loanRequest.getStatus();

        // Ambil nama status dari LoanStatus
        String status = loanStatus.getName();

        switch (status) {
            case "REVIEW" -> {
                // Cek jika user adalah marketing yang bersangkutan
                if (!currentUser.getId().equals(loanRequest.getMarketing().getId())) {
                    throw new CustomException("Hanya marketing terkait yang bisa mengakses.", HttpStatus.FORBIDDEN);
                }
            }
            case "DIREKOMENDASIKAN_MARKETING" -> {
                // Cek jika user adalah Branch Manager di cabang yang sesuai
                if (!currentUser.isBranchManager() || !currentUser.getPegawaiDetails().getBranch().getId().equals(loanRequest.getBranch().getId())) {
                    throw new CustomException("Hanya BM di cabang ini yang bisa mengakses.", HttpStatus.FORBIDDEN);
                }
            }
            case "DISETUJUI_BM" -> {
                // Cek jika user adalah Back Office di cabang yang sesuai
                if (!currentUser.isBackOffice() || !currentUser.getPegawaiDetails().getBranch().getId().equals(loanRequest.getBranch().getId())) {
                    throw new CustomException("Hanya Back Office di cabang ini yang bisa mengakses.", HttpStatus.FORBIDDEN);
                }
            }
            default -> throw new CustomException("Status tidak dikenali atau akses tidak diizinkan.", HttpStatus.FORBIDDEN);
        }
    }

    public LoanRequest getLoanRequestById(UUID id) {
        Optional<LoanRequest> loanRequestOptional = loanRequestRepository.findById(id);
        if (loanRequestOptional.isPresent()) {
            return loanRequestOptional.get();
        } else {
            throw new CustomException("LoanRequest not found", HttpStatus.NOT_FOUND);
        }
    }

    public LoanRequestApprovalDTO mapToApprovalDTO(LoanRequest loanRequest) {
        CustomerDetails customer = loanRequest.getCustomer();

        // ambil semua LoanApproval untuk request ini
        List<LoanApproval> approvals = loanApprovalService.findByLoanRequestId(loanRequest.getId());

        // catatan marketing: status = DIREKOMENDASIKAN_MARKETING atau DITOLAK_MARKETING
        String marketingNotes = approvals.stream()
                .filter(a -> "DIREKOMENDASIKAN_MARKETING".equals(a.getStatus().getName())
                        || "DITOLAK_MARKETING".equals(a.getStatus().getName()))
                .map(LoanApproval::getNotes)
                .findFirst()
                .orElse(null);

        // catatan BM: status = DISETUJUI_BM atau DITOLAK_BM
        String bmNotes = approvals.stream()
                .filter(a -> "DISETUJUI_BM".equals(a.getStatus().getName())
                        || "DITOLAK_BM".equals(a.getStatus().getName()))
                .map(LoanApproval::getNotes)
                .findFirst()
                .orElse(null);

        String backOfficeNotes = approvals.stream()
                .filter(a -> "DISBURSED".equals(a.getStatus().getName()))
                .map(LoanApproval::getNotes)
                .findFirst()
                .orElse(null);

        return LoanRequestApprovalDTO.builder()
                .id(loanRequest.getId())
                .customerJob(customer.getPekerjaan())
                .customerSalary(customer.getGaji())
                .status(loanRequest.getStatus().getName())
                .amount(loanRequest.getAmount())
                .tenor(loanRequest.getTenor())
                .requestDate(loanRequest.getRequestDate())

                .customerName(customer.getUser().getName())
                .customerKtpPhotoUrl(customer.getKtpUrl())
                .customerEmail(customer.getUser().getEmail())
                .customerPhone(customer.getNoTelp())
                .customerAddress(customer.getAlamat())

                .marketingNotes(marketingNotes)
                .bmNotes(bmNotes)
                .backOfficeNotes(backOfficeNotes)
                .build();
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
    public void reviewLoanRequest(UUID loanRequestId, UUID marketingId, String status, String notes) {
        // 1️⃣ Ambil loan request yang bersangkutan
        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new CustomException("Loan request tidak ditemukan", HttpStatus.NOT_FOUND));

        // 2️⃣ Cek apakah marketing yang login sesuai dengan yang menangani request ini
        if (!loanRequest.getMarketing().getId().equals(marketingId)) {
            throw new CustomException("Anda tidak berhak mereview loan request ini", HttpStatus.FORBIDDEN);
        }

        // 3️⃣ Update status berdasarkan hasil review
        LoanStatus loanStatus = loanStatusService.findByName(status); // Status sudah diterima sebagai parameter
        loanRequest.setStatus(loanStatus);
        if ("DIREKOMENDASIKAN_MARKETING".equals(status)) {
            loanRequest.setApprovalMarketingAt(LocalDateTime.now());
        }

        // Simpan perubahan loan request
        loanRequestRepository.save(loanRequest);

        // 4️⃣ Simpan review di LoanApproval
        LoanApproval loanApproval = LoanApproval.builder()
                .loanRequest(loanRequest)
                .handledBy(loanRequest.getMarketing()) // Marketing yang mereview
                .status(loanStatus)
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
    public void reviewLoanRequestByBM(UUID loanRequestId, UUID branchManagerId, String status, String notes) {
        // 1️⃣ Ambil loan request
        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new CustomException("Loan request tidak ditemukan", HttpStatus.NOT_FOUND));

        // 2️⃣ Cek apakah BM berhak mereview (harus di branch yang sama)
        UUID branchId = userService.getBranchIdByUserId(branchManagerId);
        if (!loanRequest.getBranch().getId().equals(branchId)) {
            throw new CustomException("Anda tidak berhak mereview loan request ini", HttpStatus.FORBIDDEN);
        }

        // 3️⃣ Update status berdasarkan review BM
        LoanStatus newStatus = loanStatusService.findByName(status); // Status sudah diterima sebagai parameter
        loanRequest.setStatus(newStatus);
        loanRequest.setApprovalBMAt(LocalDateTime.now()); // Timestamp approval BM

        // Simpan perubahan loan request
        loanRequestRepository.save(loanRequest);

        // 4️⃣ Simpan history approval BM di loan_approvals
        LoanApproval approvalRecord = LoanApproval.builder()
                .loanRequest(loanRequest)
                .handledBy(userService.findById(branchManagerId)) // BM yang approve
                .status(newStatus)
                .notes(notes)
                .approvedAt(LocalDateTime.now())
                .build();
        loanApprovalService.save(approvalRecord);
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

        // 3️⃣ Validasi apakah user ini adalah Back Office dan di cabang yang sama
        UUID branchId = userService.getBranchIdByUserId(backOfficeId);
        if (!loanRequest.getBranch().getId().equals(branchId)) {
            throw new CustomException("Anda tidak berhak mencairkan loan request ini", HttpStatus.FORBIDDEN);
        }

        // 4️⃣ Update status menjadi DISBURSED dan set waktu disbursement
        LoanStatus disbursedStatus = loanStatusService.findByName("DISBURSED");
        loanRequest.setStatus(disbursedStatus);
        loanRequest.setDisbursedAt(LocalDateTime.now());

        // 5️⃣ Hitung repayment schedule
        // (Sama seperti sebelumnya, tidak perlu diubah)

        // 6️⃣ Simpan record approval oleh BO
        LoanApproval approvalRecord = LoanApproval.builder()
                .loanRequest(loanRequest)
                .handledBy(userService.findById(backOfficeId))
                .status(disbursedStatus)
                .notes("Dana telah dicairkan.")
                .approvedAt(LocalDateTime.now())
                .build();
        loanApprovalService.save(approvalRecord);

        // 7️⃣ Simpan perubahan ke loan request
        loanRequestRepository.save(loanRequest);
    }

}

