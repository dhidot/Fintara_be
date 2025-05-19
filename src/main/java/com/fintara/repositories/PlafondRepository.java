package com.fintara.repositories;

import com.fintara.models.Plafond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PlafondRepository extends JpaRepository<Plafond, UUID> {
    Optional<Plafond> findByName(String name); // Cari berdasarkan nama

    @Query("""
    SELECT p FROM Plafond p
    WHERE p.name = 
        CASE :currentName
            WHEN 'BRONZE' THEN 'SILVER'
            WHEN 'SILVER' THEN 'GOLD'
            WHEN 'GOLD' THEN 'PLATINUM'
            ELSE NULL
        END
    """)

    Plafond findNextPlafondByName(@Param("currentName") String currentName);
}
