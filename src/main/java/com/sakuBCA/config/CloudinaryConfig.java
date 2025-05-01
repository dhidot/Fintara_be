package com.sakuBCA.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "djbksnduy",      // ganti
                "api_key", "916944492328751",            // ganti
                "api_secret", "Gx00kR-PKqWK9Z6vqh1-RB2AMBs",      // ganti
                "secure", true
        ));
    }
}
