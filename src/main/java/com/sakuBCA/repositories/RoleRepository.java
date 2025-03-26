package com.sakuBCA.repositories;

import com.sakuBCA.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    boolean existsByName(String name);

    Optional<Role> findByName(String name);

    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.roleFeatures")
    List<Role> findAllWithFeatures();

    @Query("SELECT r.name FROM Role r WHERE r.id = :roleId")
    String findRoleNameById(@Param("roleId") UUID roleId);
}