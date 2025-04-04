package com.sakuBCA.services;

import com.sakuBCA.models.Plafond;
import com.sakuBCA.repositories.PlafondRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlafondService {

    private final PlafondRepository plafondRepository;

    public List<Plafond> getAllPlafonds() {
        return plafondRepository.findAll();
    }

    public Plafond getPlafondByName(String name) {
        return plafondRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Plafond tidak ditemukan: " + name));
    }
}
