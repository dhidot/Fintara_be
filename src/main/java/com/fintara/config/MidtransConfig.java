package com.fintara.config;

import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class MidtransConfig {
    private SnapApi snapApi;

    @PostConstruct
    public void init() {
        Midtrans.serverKey = "SB-Mid-server-UsT5td_s1ZwUaq3NkBH9cuuJ";  // ganti sesuai key kamu
        Midtrans.isProduction = false;  // false untuk sandbox
    }
}
