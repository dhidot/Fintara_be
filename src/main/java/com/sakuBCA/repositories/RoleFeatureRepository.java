package com.sakuBCA.repositories;

import com.sakuBCA.models.Feature;
import com.sakuBCA.models.Role;
import com.sakuBCA.models.RoleFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleFeatureRepository extends JpaRepository<RoleFeature, Integer> {
    List<RoleFeature> findByRole(Role role);

    @Query("SELECT rf.feature.name FROM RoleFeature rf WHERE rf.role.id = :roleId")
    List<String> findFeaturesByRoleId(UUID roleId);

    Optional<RoleFeature> findByRoleAndFeature(Role role, Feature feature);

    boolean existsByRoleAndFeature(Role role, Feature feature);
}

