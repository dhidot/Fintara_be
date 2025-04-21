package com.sakuBCA.services;

import com.sakuBCA.config.exceptions.CustomException;
import com.sakuBCA.models.Plafond;
import com.sakuBCA.repositories.PlafondRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Plafond getPlafondById(UUID id) {
        return plafondRepository.findById(id)
                .orElseThrow(() -> new CustomException("Plafond tidak ditemukan", HttpStatus.NOT_FOUND));
    }

    public Plafond createPlafond(Plafond request) {
        if (plafondRepository.findByName(request.getName()).isPresent()) {
            throw new CustomException("Nama plafond sudah digunakan", HttpStatus.BAD_REQUEST);
        }
        return plafondRepository.save(request);
    }

    public Plafond updatePlafond(UUID id, Plafond request) {
        Plafond existing = plafondRepository.findById(id)
                .orElseThrow(() -> new CustomException("Plafond tidak ditemukan", HttpStatus.NOT_FOUND));

        if (!existing.getName().equals(request.getName()) &&
                plafondRepository.findByName(request.getName()).isPresent()) {
            throw new CustomException("Nama plafond sudah digunakan", HttpStatus.BAD_REQUEST);
        }

        existing.setName(request.getName());
        existing.setMaxAmount(request.getMaxAmount());
        existing.setInterestRate(request.getInterestRate());
        existing.setMinTenor(request.getMinTenor());
        existing.setMaxTenor(request.getMaxTenor());

        return plafondRepository.save(existing);
    }

    public Long count() {
        return plafondRepository.count();
    }
}
