package com.fintara.services;

import com.fintara.exceptions.CustomException;
import com.fintara.utils.NameNormalizer;
import com.fintara.models.Plafond;
import com.fintara.repositories.PlafondRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlafondService {

    private final PlafondRepository plafondRepository;
    @Autowired
    private final NameNormalizer nameNormalizer;

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
        String normalizedName = nameNormalizer.normalizedName(request.getName());
        request.setName(normalizedName);

        if (plafondRepository.findByName(request.getName()).isPresent()) {
            throw new CustomException("Nama plafond sudah digunakan", HttpStatus.BAD_REQUEST);
        }

        if (request.getMinTenor() > request.getMaxTenor()) {
            throw new CustomException("Minimal tenor tidak boleh lebih besar dari maksimal tenor", HttpStatus.BAD_REQUEST);
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

        if (request.getMinTenor() > request.getMaxTenor()) {
            throw new CustomException("Minimal tenor tidak boleh lebih besar dari maksimal tenor", HttpStatus.BAD_REQUEST);
        }

        String normalizedName = nameNormalizer.normalizedName(request.getName());
        request.setName(normalizedName);

        existing.setName(normalizedName);
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
