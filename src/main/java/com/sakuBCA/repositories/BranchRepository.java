package com.sakuBCA.repositories;

import com.sakuBCA.models.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
    boolean existsByName(String name);
    Optional<Branch> findByName(String name);
    Optional<Branch> findById(UUID id);

    @Query(value = """
        SELECT TOP 1 b.id FROM branches b
        ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(b.latitude)) 
        * cos(radians(b.longitude) - radians(:longitude)) 
        + sin(radians(:latitude)) * sin(radians(b.latitude)))) ASC
    """, nativeQuery = true)
    UUID findNearestBranch(@Param("latitude") double latitude, @Param("longitude") double longitude);
}
