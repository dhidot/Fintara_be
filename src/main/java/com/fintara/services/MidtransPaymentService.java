package com.fintara.services;

import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MidtransPaymentService {

    public String generateSnapToken(UUID repaymentScheduleId, long amount) throws MidtransError {
        Map<String, Object> params = new HashMap<>();

        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", repaymentScheduleId.toString());
        transactionDetails.put("gross_amount", amount);

        Map<String, Object> creditCard = new HashMap<>();
        creditCard.put("secure", true);

        params.put("transaction_details", transactionDetails);
        params.put("transaction_details", transactionDetails);
        params.put("credit_card", creditCard);

        // Generate token dari Midtrans
        return SnapApi.createTransactionToken(params);
    }
}
