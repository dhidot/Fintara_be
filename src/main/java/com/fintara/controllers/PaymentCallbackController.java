package com.fintara.controllers;

import com.fintara.models.RepaymentSchedule;
import com.fintara.repositories.RepaymentScheduleRepository;
import com.fintara.responses.ApiResponse;
import com.fintara.services.MidtransPaymentService;
import com.midtrans.Midtrans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("v1/payments")
public class PaymentCallbackController {

    @Autowired
    private RepaymentScheduleRepository repaymentScheduleRepository;

    @Autowired
    private MidtransPaymentService midtransPaymentService;

    @PostMapping("/callback")
    public ResponseEntity<String> midtransCallback(@RequestBody Map<String, Object> payload) {
        String orderId = (String) payload.get("order_id");
        String transactionStatus = (String) payload.get("transaction_status");
        String statusCode = (String) payload.get("status_code");
        String grossAmount = (String) payload.get("gross_amount");
        String signatureKey = (String) payload.get("signature_key");

        // Verifikasi signature
        if (!isValidSignature(orderId, statusCode, grossAmount, Midtrans.serverKey, signatureKey)) {
            return ResponseEntity.status(403).body("Invalid signature");
        }

        // Update repayment schedule
        RepaymentSchedule schedule = repaymentScheduleRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        if ("capture".equals(transactionStatus) || "settlement".equals(transactionStatus)) {
            schedule.setAmountPaid(new BigDecimal(grossAmount));
            schedule.setPaidAt(LocalDate.now());
            schedule.setIsLate(false);
            repaymentScheduleRepository.save(schedule);
        } else if ("cancel".equals(transactionStatus) || "deny".equals(transactionStatus) || "expire".equals(transactionStatus)) {
            // Tambahkan logic jika perlu
        }

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/generate-token")
    public ResponseEntity<ApiResponse<String>> generateSnapToken(@RequestBody Map<String, Object> request) {
        try {
            String repaymentScheduleId = (String) request.get("repaymentScheduleId");
            UUID uuid = UUID.fromString(repaymentScheduleId);

            RepaymentSchedule schedule = repaymentScheduleRepository.findById(uuid)
                    .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

            long amount = schedule.getAmountToPay().longValue();

            String snapToken = midtransPaymentService.generateSnapToken(uuid, amount);
            System.out.println("Generated Snap Token: " + snapToken);

            // Pakai ApiResponse.success()
            return ResponseEntity.ok(ApiResponse.success("Token generated successfully", snapToken));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

    private boolean isValidSignature(String orderId, String statusCode, String grossAmount, String serverKey, String signatureKey) {
        String payload = orderId + statusCode + grossAmount + serverKey;
        String hash = sha512(payload);
        return hash.equals(signatureKey);
    }

    private String sha512(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
