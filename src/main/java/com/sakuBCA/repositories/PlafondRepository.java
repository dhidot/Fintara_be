package com.sakuBCA.repositories;

import com.sakuBCA.models.Plafond;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlafondRepository extends JpaRepository<Plafond, UUID> {
    Optional<Plafond> findByName(String name); // Cari berdasarkan nama
}
