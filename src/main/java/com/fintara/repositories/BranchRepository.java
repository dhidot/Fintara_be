package com.fintara.repositories;

import com.fintara.models.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
    boolean existsByName(String name);
    Optional<Branch> findByName(String name);
    Optional<Branch> findById(UUID id);
    List<Branch> findAll();
}
