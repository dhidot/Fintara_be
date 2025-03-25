package com.sakuBCA.repositories;

import com.sakuBCA.models.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
    boolean existsByName(String name);
    Optional<Branch> findByName(String name);
}
