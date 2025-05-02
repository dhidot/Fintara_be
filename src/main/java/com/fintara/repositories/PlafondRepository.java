package com.fintara.repositories;

import com.fintara.models.Plafond;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlafondRepository extends JpaRepository<Plafond, UUID> {
    Optional<Plafond> findByName(String name); // Cari berdasarkan nama
}
