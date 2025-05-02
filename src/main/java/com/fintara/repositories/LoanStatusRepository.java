package com.fintara.repositories;

import com.fintara.models.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanStatusRepository extends JpaRepository<LoanStatus, UUID> {
    Optional<LoanStatus> findByName(String name);
}

