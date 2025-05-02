package com.sakuBCA.repositories;

import com.sakuBCA.models.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
    boolean existsByName(String name);
    Optional<Branch> findByName(String name);
    Optional<Branch> findById(UUID id);
    List<Branch> findAll();
}
